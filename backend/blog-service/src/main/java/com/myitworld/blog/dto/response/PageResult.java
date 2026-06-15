package com.myitworld.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    private List<T> records;
    private long total;
    private long page;
    private long size;
}
