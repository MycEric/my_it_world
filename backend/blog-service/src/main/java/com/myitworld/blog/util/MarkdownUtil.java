package com.myitworld.blog.util;

/**
 * Markdown 工具类
 * <p>
 * 提供摘要自动生成、从 .md 文件解析标题等能力。
 * </p>
 */
public final class MarkdownUtil {

    private MarkdownUtil() {
    }

    /**
     * 从 Markdown 正文自动生成摘要
     * <p>
     * 去除 # 标题、代码块、多余空白后截取前 maxLen 个字符。
     * </p>
     */
    public static String generateSummary(String markdown, int maxLen) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        String text = markdown
                .replaceAll("(?s)```.*?```", " ")
                .replaceAll("#+\\s*", "")
                .replaceAll("[*_>`\\[\\]()#]", "")
                .replaceAll("\\s+", " ")
                .trim();
        if (text.length() <= maxLen) {
            return text;
        }
        return text.substring(0, maxLen) + "...";
    }

    /**
     * 从 Markdown 首行 # 标题解析标题（用于 .md 文件导入）
     * <p>
     * 若第一行不是 # 标题，返回 null，由调用方使用文件名或手动指定。
     * </p>
     */
    public static String extractTitleFromMarkdown(String markdown) {
        if (markdown == null) {
            return null;
        }
        for (String line : markdown.lines().toList()) {
            String trimmed = line.trim();
            if (trimmed.startsWith("# ")) {
                return trimmed.substring(2).trim();
            }
            if (!trimmed.isEmpty()) {
                break;
            }
        }
        return null;
    }
}
