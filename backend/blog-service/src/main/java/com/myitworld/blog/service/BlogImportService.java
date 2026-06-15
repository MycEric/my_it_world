package com.myitworld.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myitworld.blog.config.BlogImportProperties;
import com.myitworld.blog.dto.request.BatchImportRequest;
import com.myitworld.blog.dto.response.BatchImportResult;
import com.myitworld.blog.entity.BlogArticle;
import com.myitworld.blog.enums.ArticleSource;
import com.myitworld.blog.enums.ArticleStatus;
import com.myitworld.blog.mapper.BlogArticleMapper;
import com.myitworld.blog.util.BlogIndexItem;
import com.myitworld.blog.util.ImportMarkdownUtil;
import com.myitworld.blog.util.MarkdownUtil;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CSDN 导出包批量导入服务
 * <p>
 * 读取 output/index.json + articles/*.md，图片仍放在 output/images/ 下，
 * 通过 /api/blogs/assets/** 静态访问；Markdown 内路径会自动改写。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogImportService {

    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final BlogArticleMapper articleMapper;
    private final BlogImportProperties importProperties;
    private final ObjectMapper objectMapper;

    /**
     * 批量导入 index.json 中全部文章
     */
    public BatchImportResult batchImport(BatchImportRequest request) {
        Path outputRoot = resolveOutputRoot();
        Path indexFile = outputRoot.resolve("index.json");
        if (!Files.isRegularFile(indexFile)) {
            throw new BusinessException(ResultCode.BAD_REQUEST,
                    "未找到 index.json，请检查 blog.import.output-dir: " + outputRoot.toAbsolutePath());
        }

        List<BlogIndexItem> items;
        try {
            items = objectMapper.readValue(indexFile.toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "解析 index.json 失败: " + e.getMessage());
        }

        int imported = 0, skipped = 0, updated = 0, failed = 0;
        List<String> errors = new ArrayList<>();

        for (BlogIndexItem item : items) {
            try {
                ImportAction action = importOne(outputRoot, item, request);
                switch (action) {
                    case IMPORTED -> imported++;
                    case SKIPPED -> skipped++;
                    case UPDATED -> updated++;
                }
            } catch (Exception e) {
                failed++;
                log.warn("导入失败 id={}, title={}: {}", item.id(), item.title(), e.getMessage());
                errors.add(item.id() + " " + item.title() + ": " + e.getMessage());
            }
        }

        log.info("批量导入完成: total={}, imported={}, skipped={}, updated={}, failed={}",
                items.size(), imported, skipped, updated, failed);

        return BatchImportResult.builder()
                .total(items.size())
                .imported(imported)
                .skipped(skipped)
                .updated(updated)
                .failed(failed)
                .errors(errors)
                .build();
    }

    private enum ImportAction { IMPORTED, SKIPPED, UPDATED }

    private ImportAction importOne(Path outputRoot, BlogIndexItem item, BatchImportRequest request) throws IOException {
        BlogArticle existing = articleMapper.selectOne(
                new LambdaQueryWrapper<BlogArticle>()
                        .eq(BlogArticle::getSource, ArticleSource.CSDN.getCode())
                        .eq(BlogArticle::getSourceId, item.id()));

        if (existing != null && request.isSkipExisting() && !request.isUpdateExisting()) {
            return ImportAction.SKIPPED;
        }

        Path mdPath = resolveMarkdownPath(outputRoot, item);
        if (!Files.isRegularFile(mdPath)) {
            throw new IOException("Markdown 文件不存在: " + mdPath);
        }

        String raw = Files.readString(mdPath, StandardCharsets.UTF_8);
        String body = ImportMarkdownUtil.stripFrontmatter(raw);
        String content = ImportMarkdownUtil.rewriteImageUrls(body, importProperties.getAssetsUrlPrefix());
        String summary = MarkdownUtil.generateSummary(content, 200);

        int status = request.isPublish()
                ? ArticleStatus.PUBLISHED.getCode()
                : ArticleStatus.DRAFT.getCode();
        LocalDateTime publishTime = parsePublishTime(item.publishTime());

        if (existing != null && request.isUpdateExisting()) {
            existing.setTitle(item.title());
            existing.setContent(content);
            existing.setSummary(summary);
            existing.setSourceUrl(item.url());
            existing.setStatus(status);
            if (publishTime != null) {
                existing.setPublishTime(publishTime);
            }
            articleMapper.updateById(existing);
            return ImportAction.UPDATED;
        }

        if (existing != null) {
            return ImportAction.SKIPPED;
        }

        BlogArticle article = new BlogArticle();
        article.setTitle(item.title());
        article.setSummary(summary);
        article.setContent(content);
        article.setSource(ArticleSource.CSDN.getCode());
        article.setSourceId(item.id());
        article.setSourceUrl(item.url());
        article.setStatus(status);
        article.setPublishTime(publishTime != null ? publishTime : LocalDateTime.now());
        article.setViewCount(0);
        article.setAuthorId(0L);
        article.setAuthorName("CSDN Import");
        articleMapper.insert(article);
        return ImportAction.IMPORTED;
    }

    /** 解析 output 根目录 */
    public Path resolveOutputRoot() {
        Path path = Paths.get(importProperties.getOutputDir()).normalize();
        if (path.isAbsolute()) {
            return path;
        }
        return Paths.get(System.getProperty("user.dir")).resolve(path).normalize();
    }

    /** 图片静态资源目录：output/images/ */
    public Path resolveImagesRoot() {
        return resolveOutputRoot().resolve("images").normalize();
    }

    private Path resolveMarkdownPath(Path outputRoot, BlogIndexItem item) {
        if (StringUtils.hasText(item.path())) {
            return outputRoot.resolve(item.path().replace('\\', '/')).normalize();
        }
        return outputRoot.resolve("articles").resolve(item.filename()).normalize();
    }

    private LocalDateTime parsePublishTime(String publishTime) {
        if (!StringUtils.hasText(publishTime)) {
            return null;
        }
        try {
            return LocalDateTime.parse(publishTime, ISO_FORMAT);
        } catch (Exception e) {
            log.debug("解析发布时间失败: {}", publishTime);
            return null;
        }
    }
}
