package com.myitworld.blog.controller;

import com.myitworld.blog.dto.request.BatchImportRequest;
import com.myitworld.blog.dto.request.BlogArticleSaveRequest;
import com.myitworld.blog.dto.response.BatchImportResult;
import com.myitworld.blog.dto.response.BlogArticleDetail;
import com.myitworld.blog.dto.response.BlogArticleListItem;
import com.myitworld.blog.dto.response.PageResult;
import com.myitworld.blog.service.BlogImportService;
import com.myitworld.blog.service.BlogService;
import com.myitworld.common.constant.AuthConstants;
import com.myitworld.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 博客管理端接口
 * <p>
 * 路径前缀 /api/blogs/admin/**，需 Gateway JWT + ADMIN 角色。
 * 支持 Markdown 在线保存、发布/下架、.md 文件导入。
 * </p>
 */
@Tag(name = "博客-管理", description = "Admin 博客 CRUD / 发布 / 导入")
@RestController
@RequestMapping("/api/blogs/admin")
@RequiredArgsConstructor
public class BlogAdminController {

    private final BlogService blogService;
    private final BlogImportService blogImportService;

    @Operation(summary = "批量导入 CSDN 导出包（index.json + articles）")
    @PostMapping("/import/batch")
    public Result<BatchImportResult> batchImport(@RequestBody(required = false) BatchImportRequest request) {
        if (request == null) {
            request = new BatchImportRequest();
        }
        return Result.success(blogImportService.batchImport(request));
    }

    @Operation(summary = "管理端分页列表")
    @GetMapping
    public Result<PageResult<BlogArticleListItem>> page(
            @RequestParam(value = "page", defaultValue = "1") long page,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(blogService.pageAdmin(page, size, keyword, status));
    }

    @Operation(summary = "管理端文章详情")
    @GetMapping("/{id}")
    public Result<BlogArticleDetail> detail(@PathVariable("id") Long id) {
        return Result.success(blogService.getAdminDetail(id));
    }

    @Operation(summary = "新建文章")
    @PostMapping
    public Result<Map<String, Long>> create(
            @Valid @RequestBody BlogArticleSaveRequest request,
            @RequestHeader(value = AuthConstants.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthConstants.HEADER_USERNAME, required = false) String username) {
        Long authorId = parseUserId(userIdHeader);
        Long id = blogService.create(request, authorId, username != null ? username : "admin");
        return Result.success(Map.of("id", id));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable("id") Long id,
                               @Valid @RequestBody BlogArticleSaveRequest request) {
        blogService.update(id, request);
        return Result.success();
    }

    @Operation(summary = "发布文章")
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable("id") Long id) {
        blogService.publish(id);
        return Result.success();
    }

    @Operation(summary = "下架文章")
    @PostMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable("id") Long id) {
        blogService.offline(id);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        blogService.delete(id);
        return Result.success();
    }

    @Operation(summary = "导入 Markdown 文件")
    @PostMapping("/import-md")
    public Result<Map<String, Long>> importMarkdown(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "status", required = false, defaultValue = "0") Integer status,
            @RequestHeader(value = AuthConstants.HEADER_USER_ID, required = false) String userIdHeader,
            @RequestHeader(value = AuthConstants.HEADER_USERNAME, required = false) String username)
            throws IOException {
        Long authorId = parseUserId(userIdHeader);
        Long id = blogService.importMarkdown(file, title, status, authorId,
                username != null ? username : "admin");
        return Result.success(Map.of("id", id));
    }

    private Long parseUserId(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            return 0L;
        }
        return Long.parseLong(userIdHeader);
    }
}
