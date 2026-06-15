package com.myitworld.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myitworld.blog.entity.BlogArticle;
import com.myitworld.blog.entity.BlogCategory;
import com.myitworld.blog.enums.ArticleSource;
import com.myitworld.blog.enums.ArticleStatus;
import com.myitworld.blog.mapper.BlogArticleMapper;
import com.myitworld.blog.mapper.BlogCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 博客示例数据初始化
 * <p>
 * 首次启动且无已发布文章时，插入一篇 Markdown 示例博客，便于前台演示。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BlogDataInitializer implements CommandLineRunner {

    private final BlogArticleMapper articleMapper;
    private final BlogCategoryMapper categoryMapper;

    @Override
    public void run(String... args) {
        initCategories();
        initSampleArticle();
    }

    private void initCategories() {
        if (categoryMapper.selectCount(null) > 0) {
            return;
        }
        insertCategory("Java", "java", 1);
        insertCategory("Spring Cloud", "spring-cloud", 2);
        insertCategory("前端", "frontend", 3);
        log.info("初始化博客分类");
    }

    private void insertCategory(String name, String slug, int sort) {
        BlogCategory c = new BlogCategory();
        c.setName(name);
        c.setSlug(slug);
        c.setSortOrder(sort);
        categoryMapper.insert(c);
    }

    private void initSampleArticle() {
        Long publishedCount = articleMapper.selectCount(
                new LambdaQueryWrapper<BlogArticle>().eq(BlogArticle::getStatus, ArticleStatus.PUBLISHED.getCode()));
        if (publishedCount > 0) {
            return;
        }

        String markdown = """
                # 欢迎来到 My IT World 博客

                本站采用 **模式 A：MySQL 存储 Markdown** 实现博客功能。

                ## 功能特性

                - Admin 后台在线编写 Markdown 文章
                - 支持草稿 / 发布 / 下架
                - 支持上传 `.md` 文件导入
                - 前端使用 react-markdown 渲染

                ## 代码示例

                ```java
                @PostMapping
                public Result<Long> create(@RequestBody BlogArticleSaveRequest request) {
                    // content 字段直接存 Markdown 原文
                    return blogService.create(request);
                }
                ```

                > 后续可扩展 CSDN RSS 同步与 AI 向量问答。
                """;

        BlogArticle article = new BlogArticle();
        article.setTitle("欢迎来到 My IT World 博客");
        article.setSummary("介绍本站博客模块的 Markdown 存储与发布能力");
        article.setContent(markdown);
        article.setSource(ArticleSource.LOCAL.getCode());
        article.setStatus(ArticleStatus.PUBLISHED.getCode());
        article.setPublishTime(LocalDateTime.now());
        article.setViewCount(0);
        article.setAuthorId(0L);
        article.setAuthorName("admin");
        articleMapper.insert(article);
        log.info("初始化示例博客: id={}", article.getId());
    }
}
