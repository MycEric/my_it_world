package com.myitworld.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建/更新博客文章请求
 * <p>
 * content 为 Markdown 原文，直接存入 MySQL LONGTEXT 字段。
 * </p>
 */
@Data
@Schema(description = "博客文章保存请求")
public class BlogArticleSaveRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长 200 字符")
    private String title;

    @Size(max = 500, message = "摘要最长 500 字符")
    private String summary;

    @NotBlank(message = "正文不能为空")
    private String content;

    @Size(max = 512, message = "封面 URL 过长")
    private String cover;

    /** 0草稿 1发布 2下架；新建时传 0 或 1 */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /** 分类 ID 列表，可为空 */
    private List<Long> categoryIds;
}
