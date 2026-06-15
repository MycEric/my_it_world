package com.myitworld.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 博客批量导入配置
 * <p>
 * 指向 CSDN 导出目录 backend/output，包含 index.json、articles/、images/。
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.import")
public class BlogImportProperties {

    /**
     * 导出根目录，默认 backend/output（相对 blog-service 运行目录或绝对路径）
     */
    private String outputDir = "../output";

    /** 导入后是否默认发布（1=已发布） */
    private boolean publishOnImport = true;

    /** 图片 URL 前缀，Markdown 内相对路径会改写为此前缀 */
    private String assetsUrlPrefix = "/api/blogs/assets";
}
