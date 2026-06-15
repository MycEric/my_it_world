package com.myitworld.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myitworld.blog.dto.request.BlogArticleSaveRequest;
import com.myitworld.blog.dto.response.BlogArticleDetail;
import com.myitworld.blog.dto.response.BlogArticleListItem;
import com.myitworld.blog.dto.response.PageResult;
import com.myitworld.blog.entity.BlogArticle;
import com.myitworld.blog.entity.BlogArticleCategory;
import com.myitworld.blog.entity.BlogCategory;
import com.myitworld.blog.enums.ArticleSource;
import com.myitworld.blog.enums.ArticleStatus;
import com.myitworld.blog.mapper.BlogArticleCategoryMapper;
import com.myitworld.blog.mapper.BlogArticleMapper;
import com.myitworld.blog.mapper.BlogCategoryMapper;
import com.myitworld.blog.util.MarkdownUtil;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 博客核心业务服务
 * <p>
 * 模式 A：Markdown 原文存 MySQL，类似 CSDN 的「元数据 + 正文 + 状态」模型。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogArticleMapper articleMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogArticleCategoryMapper articleCategoryMapper;

    /** 公开分页列表（仅已发布） */
    public PageResult<BlogArticleListItem> pagePublished(long page, long size, String keyword, Long categoryId) {
        IPage<BlogArticle> result = articleMapper.selectPublishedPage(
                new Page<>(page, size), keyword, categoryId);
        List<BlogArticleListItem> items = result.getRecords().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        return new PageResult<>(items, result.getTotal(), page, size);
    }

    /** 管理端分页（含草稿/下架） */
    public PageResult<BlogArticleListItem> pageAdmin(long page, long size, String keyword, Integer status) {
        IPage<BlogArticle> result = articleMapper.selectAdminPage(
                new Page<>(page, size), keyword, status);
        List<BlogArticleListItem> items = result.getRecords().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        return new PageResult<>(items, result.getTotal(), page, size);
    }

    /** 最新已发布文章 */
    public List<BlogArticleListItem> latest(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        return articleMapper.selectLatestPublished(safeLimit).stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
    }

    /** 公开详情：仅已发布；浏览量 +1 */
    public BlogArticleDetail getPublishedDetail(Long id) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != ArticleStatus.PUBLISHED.getCode()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在或未发布");
        }
        articleMapper.incrementViewCount(id);
        article.setViewCount(article.getViewCount() == null ? 1 : article.getViewCount() + 1);
        return toDetail(article);
    }

    /** 管理端详情：任意状态可见 */
    public BlogArticleDetail getAdminDetail(Long id) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        return toDetail(article);
    }

    /** 新建文章 */
    @Transactional(rollbackFor = Exception.class)
    public Long create(BlogArticleSaveRequest request, Long authorId, String authorName) {
        BlogArticle article = buildArticleFromRequest(new BlogArticle(), request);
        article.setSource(ArticleSource.LOCAL.getCode());
        article.setAuthorId(authorId);
        article.setAuthorName(authorName);
        article.setViewCount(0);
        applyPublishTime(article, request.getStatus());
        articleMapper.insert(article);
        saveCategories(article.getId(), request.getCategoryIds());
        log.info("新建博客: id={}, title={}, author={}", article.getId(), article.getTitle(), authorName);
        return article.getId();
    }

    /** 更新文章 */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, BlogArticleSaveRequest request) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        Integer oldStatus = article.getStatus();
        buildArticleFromRequest(article, request);
        if (request.getStatus() == ArticleStatus.PUBLISHED.getCode()
                && (oldStatus == null || oldStatus != ArticleStatus.PUBLISHED.getCode())) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(article);
        articleCategoryMapper.deleteByArticleId(id);
        saveCategories(id, request.getCategoryIds());
        log.info("更新博客: id={}, title={}", id, article.getTitle());
    }

    /** 发布文章（草稿/下架 → 已发布） */
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        BlogArticle article = requireArticle(id);
        article.setStatus(ArticleStatus.PUBLISHED.getCode());
        if (article.getPublishTime() == null) {
            article.setPublishTime(LocalDateTime.now());
        }
        articleMapper.updateById(article);
        log.info("发布博客: id={}", id);
    }

    /** 下架文章 */
    @Transactional(rollbackFor = Exception.class)
    public void offline(Long id) {
        BlogArticle article = requireArticle(id);
        article.setStatus(ArticleStatus.OFFLINE.getCode());
        articleMapper.updateById(article);
        log.info("下架博客: id={}", id);
    }

    /** 逻辑删除 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BlogArticle article = requireArticle(id);
        articleMapper.deleteById(article.getId());
        articleCategoryMapper.deleteByArticleId(id);
        log.info("删除博客: id={}", id);
    }

    /**
     * 导入 Markdown 文件
     * <p>
     * 读取 .md 文件 UTF-8 内容；若首行是 # 标题则自动解析为 title。
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public Long importMarkdown(MultipartFile file, String title, Integer status,
                               Long authorId, String authorName) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName != null && !originalName.toLowerCase().endsWith(".md")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仅支持 .md 文件");
        }
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件内容为空");
        }

        BlogArticleSaveRequest request = new BlogArticleSaveRequest();
        request.setContent(content);
        request.setTitle(StringUtils.hasText(title) ? title : resolveTitle(content, originalName));
        request.setSummary(MarkdownUtil.generateSummary(content, 200));
        request.setStatus(status != null ? status : ArticleStatus.DRAFT.getCode());
        return create(request, authorId, authorName);
    }

    /** 全部分类列表 */
    public List<BlogCategory> listCategories() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<BlogCategory>().orderByAsc(BlogCategory::getSortOrder));
    }

    // ======================== 私有方法 ========================

    private BlogArticle requireArticle(Long id) {
        BlogArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        return article;
    }

    private BlogArticle buildArticleFromRequest(BlogArticle article, BlogArticleSaveRequest request) {
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCover(request.getCover());
        article.setStatus(request.getStatus());
        if (StringUtils.hasText(request.getSummary())) {
            article.setSummary(request.getSummary());
        } else {
            article.setSummary(MarkdownUtil.generateSummary(request.getContent(), 200));
        }
        return article;
    }

    private void applyPublishTime(BlogArticle article, Integer status) {
        if (status != null && status == ArticleStatus.PUBLISHED.getCode()) {
            article.setPublishTime(LocalDateTime.now());
        }
    }

    private void saveCategories(Long articleId, List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        for (Long categoryId : categoryIds) {
            BlogArticleCategory rel = new BlogArticleCategory();
            rel.setArticleId(articleId);
            rel.setCategoryId(categoryId);
            articleCategoryMapper.insert(rel);
        }
    }

    private String resolveTitle(String content, String filename) {
        String fromMd = MarkdownUtil.extractTitleFromMarkdown(content);
        if (StringUtils.hasText(fromMd)) {
            return fromMd;
        }
        if (filename != null && filename.endsWith(".md")) {
            return filename.substring(0, filename.length() - 3);
        }
        return "未命名文章";
    }

    private BlogArticleListItem toListItem(BlogArticle article) {
        List<BlogCategory> categories = categoryMapper.selectByArticleId(article.getId());
        return BlogArticleListItem.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .cover(article.getCover())
                .source(article.getSource())
                .status(article.getStatus())
                .publishTime(article.getPublishTime())
                .viewCount(article.getViewCount())
                .authorName(article.getAuthorName())
                .categories(categories.stream().map(c -> BlogArticleListItem.CategoryItem.builder()
                        .id(c.getId()).name(c.getName()).slug(c.getSlug()).build()).toList())
                .build();
    }

    private BlogArticleDetail toDetail(BlogArticle article) {
        List<BlogCategory> categories = categoryMapper.selectByArticleId(article.getId());
        return BlogArticleDetail.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(article.getSummary())
                .content(article.getContent())
                .cover(article.getCover())
                .source(article.getSource())
                .sourceUrl(article.getSourceUrl())
                .status(article.getStatus())
                .publishTime(article.getPublishTime())
                .viewCount(article.getViewCount())
                .authorName(article.getAuthorName())
                .createTime(article.getCreateTime())
                .updateTime(article.getUpdateTime())
                .categories(categories.stream().map(c -> BlogArticleListItem.CategoryItem.builder()
                        .id(c.getId()).name(c.getName()).slug(c.getSlug()).build()).toList())
                .build();
    }
}
