package com.myitworld.blog.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导入用 Markdown 处理：去 frontmatter、改写图片路径
 */
public final class ImportMarkdownUtil {

    private static final Pattern FRONTMATTER = Pattern.compile("^---\\s*\\n[\\s\\S]*?\\n---\\s*\\n", Pattern.MULTILINE);
    private static final Pattern IMAGE_PATH = Pattern.compile("(!\\[[^\\]]*]\\()\\s*images[/\\\\](\\d+)[/\\\\]([^)]+)\\)", Pattern.CASE_INSENSITIVE);

    private ImportMarkdownUtil() {
    }

    /** 去掉 YAML frontmatter（CSDN 导出 md 文件头部） */
    public static String stripFrontmatter(String markdown) {
        if (markdown == null) {
            return "";
        }
        return FRONTMATTER.matcher(markdown).replaceFirst("").trim();
    }

    /**
     * 将 Markdown 内 images/{articleId}/xx.png 改为可访问的 HTTP 路径
     * 例：images/109724211/01.png → /api/blogs/assets/109724211/01.png
     */
    public static String rewriteImageUrls(String markdown, String assetsUrlPrefix) {
        if (!StringUtils.hasText(markdown)) {
            return markdown;
        }
        Matcher matcher = IMAGE_PATH.matcher(markdown);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String prefix = matcher.group(1);
            String articleId = matcher.group(2);
            String fileName = matcher.group(3);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(
                    prefix + assetsUrlPrefix + "/" + articleId + "/" + fileName + ")"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
