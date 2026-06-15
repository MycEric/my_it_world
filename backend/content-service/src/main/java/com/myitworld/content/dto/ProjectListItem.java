package com.myitworld.content.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectListItem {

    private Long id;
    private String name;
    private String description;
    private String coverUrl;
    private String githubUrl;
    private String demoUrl;
    private Integer featured;
    private Integer sortOrder;
    private Integer status;
    private List<String> techStack;
    private LocalDateTime updateTime;
}
