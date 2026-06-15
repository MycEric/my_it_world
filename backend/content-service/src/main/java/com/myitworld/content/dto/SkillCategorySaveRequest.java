package com.myitworld.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillCategorySaveRequest {

    @NotBlank
    @Size(max = 64)
    private String name;

    private Integer sortOrder = 0;
}
