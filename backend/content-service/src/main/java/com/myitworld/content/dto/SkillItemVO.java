package com.myitworld.content.dto;

import lombok.Data;

@Data
public class SkillItemVO {

    private Long id;
    private Long categoryId;
    private String name;
    private Integer level;
    private String iconUrl;
    private Integer sortOrder;
    private Integer featured;
}
