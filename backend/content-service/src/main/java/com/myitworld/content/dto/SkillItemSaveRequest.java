package com.myitworld.content.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillItemSaveRequest {

    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 64)
    private String name;

    @Min(1)
    @Max(5)
    private Integer level = 3;

    @Size(max = 512)
    private String iconUrl;

    private Integer sortOrder = 0;

    private Integer featured = 0;
}
