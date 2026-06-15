package com.myitworld.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客列表项（不含完整正文，减小传输体积）
 */
@Data
@Builder
@Schema(description = "博客列表项")
public class BlogArticleListItem {

    private Long id;
    private String title;
    private String summary;
    private String cover;
    private String source;
    private Integer status;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private String authorName;
    private List<CategoryItem> categories;

    @Data
    @Builder
    public static class CategoryItem {
        private Long id;
        private String name;
        private String slug;
    }
}
