package com.myitworld.blog.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量导入结果统计
 */
@Data
@Builder
@Schema(description = "批量导入结果")
public class BatchImportResult {

    private int total;
    private int imported;
    private int skipped;
    private int updated;
    private int failed;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
