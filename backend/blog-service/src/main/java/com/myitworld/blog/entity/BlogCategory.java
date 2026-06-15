package com.myitworld.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客分类实体
 */
@Data
@TableName("blog_category")
public class BlogCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** URL 友好标识，如 java、spring-cloud */
    private String slug;

    /** 排序，越小越靠前 */
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
