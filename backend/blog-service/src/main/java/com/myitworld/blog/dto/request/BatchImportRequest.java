package com.myitworld.blog.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 批量导入 CSDN 导出包请求
 */
@Data
@Schema(description = "批量导入请求")
public class BatchImportRequest {

    /** 是否跳过已存在（按 source=CSDN + source_id 判断） */
    @Schema(description = "跳过已导入文章", defaultValue = "true")
    private boolean skipExisting = true;

    /** 是否更新已存在文章的正文与标题 */
    @Schema(description = "更新已存在文章", defaultValue = "false")
    private boolean updateExisting = false;

    /** 导入后状态：true=已发布，false=草稿 */
    @Schema(description = "导入即发布", defaultValue = "true")
    private boolean publish = true;
}
