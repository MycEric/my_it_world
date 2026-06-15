package com.myitworld.blog.controller;

import com.myitworld.blog.dto.response.BlogArticleDetail;
import com.myitworld.blog.dto.response.BlogArticleListItem;
import com.myitworld.blog.dto.response.PageResult;
import com.myitworld.blog.entity.BlogCategory;
import com.myitworld.blog.service.BlogService;
import com.myitworld.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 博客公开读接口
 * <p>
 * 无需登录（Gateway 白名单放行）：
 * - 分页列表、详情、最新文章、分类列表
 * 仅返回 status=已发布 的文章（详情接口除外由 Service 控制）。
 * </p>
 */
@Tag(name = "博客-公开", description = "博客列表、详情、最新")
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @Operation(summary = "分页列表（已发布）")
    @GetMapping
    public Result<PageResult<BlogArticleListItem>> page(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return Result.success(blogService.pagePublished(page, size, keyword, categoryId));
    }

    @Operation(summary = "最新文章")
    @GetMapping("/latest")
    public Result<List<BlogArticleListItem>> latest(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return Result.success(blogService.latest(limit));
    }

    @Operation(summary = "分类列表")
    @GetMapping("/categories")
    public Result<List<BlogCategory>> categories() {
        return Result.success(blogService.listCategories());
    }

    @Operation(summary = "文章详情（已发布）")
    @GetMapping("/{id}")
    public Result<BlogArticleDetail> detail(@PathVariable("id") Long id) {
        return Result.success(blogService.getPublishedDetail(id));
    }
}
