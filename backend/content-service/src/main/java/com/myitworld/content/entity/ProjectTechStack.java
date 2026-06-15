package com.myitworld.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("project_tech_stack")
public class ProjectTechStack {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long projectId;
    private String techName;
    private Integer sortOrder;
}
