package com.myitworld.blog.util;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 对应 output/index.json 中单条记录
 */
public record BlogIndexItem(
        String id,
        String title,
        String filename,
        String path,
        String url,
        @JsonProperty("publish_time") String publishTime
) {
}
