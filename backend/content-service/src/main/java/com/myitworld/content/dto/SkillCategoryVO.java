package com.myitworld.content.dto;

import lombok.Data;

import java.util.List;

@Data
public class SkillCategoryVO {

    private Long id;
    private String name;
    private Integer sortOrder;
    private List<SkillItemVO> items;
}
