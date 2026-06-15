package com.myitworld.blog.enums;

import lombok.Getter;

/**
 * 博客文章来源
 * <p>
 * LOCAL：本站 Markdown 编辑器/上传创建
 * CSDN：后续 RSS 同步扩展（预留）
 * </p>
 */
@Getter
public enum ArticleSource {

    LOCAL("LOCAL", "本站创作"),
    CSDN("CSDN", "CSDN 同步");

    private final String code;
    private final String label;

    ArticleSource(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
