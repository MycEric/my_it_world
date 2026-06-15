package com.myitworld.blog.enums;

import lombok.Getter;

/**
 * 博客文章状态
 * <p>
 * 类似 CSDN 的草稿/发布/下架流程。
 * </p>
 */
@Getter
public enum ArticleStatus {

    /** 草稿：仅 Admin 可见，前台不展示 */
    DRAFT(0, "草稿"),

    /** 已发布：前台列表与详情可见 */
    PUBLISHED(1, "已发布"),

    /** 已下架：前台不可见，Admin 可重新发布 */
    OFFLINE(2, "已下架");

    private final int code;
    private final String label;

    ArticleStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static ArticleStatus fromCode(int code) {
        for (ArticleStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知文章状态: " + code);
    }
}
