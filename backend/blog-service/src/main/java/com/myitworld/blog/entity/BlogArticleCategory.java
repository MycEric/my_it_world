package com.myitworld.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章-分类关联（多对多中间表）
 */
@Data
@TableName("blog_article_category")
public class BlogArticleCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long categoryId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
