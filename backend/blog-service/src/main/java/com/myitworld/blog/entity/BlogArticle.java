package com.myitworld.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客文章实体
 * <p>
 * 核心设计（模式 A）：content 字段存储 Markdown 原文（LONGTEXT），
 * 不做 HTML 转换，由前端 react-markdown 渲染展示。
 * </p>
 */
@Data
@TableName("blog_article")
public class BlogArticle {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    private String title;

    /** 摘要，列表页展示；为空时 Service 从 Markdown 自动截取 */
    private String summary;

    /** Markdown 正文原文 */
    private String content;

    /** 封面图 URL */
    private String cover;

    /** 来源：LOCAL / CSDN */
    private String source;

    /** 外部来源 ID（CSDN 文章 ID，用于去重，本站创作可为空） */
    private String sourceId;

    /** 原文外链（CSDN 链接等） */
    private String sourceUrl;

    /** 状态：0草稿 1已发布 2已下架 */
    private Integer status;

    /** 发布时间（status=1 时写入） */
    private LocalDateTime publishTime;

    /** 浏览次数 */
    private Integer viewCount;

    /** 作者用户 ID（来自网关 X-User-Id） */
    private Long authorId;

    /** 作者用户名（冗余，便于展示） */
    private String authorName;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
