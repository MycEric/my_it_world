package com.myitworld.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客详情（含 Markdown 正文）
 */
@Data
@Builder
@Schema(description = "博客详情")
public class BlogArticleDetail {

    private Long id;
    private String title;
    private String summary;
    /** Markdown 原文，前端 react-markdown 渲染 */
    private String content;
    private String cover;
    private String source;
    private String sourceUrl;
    private Integer status;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<BlogArticleListItem.CategoryItem> categories;
}
