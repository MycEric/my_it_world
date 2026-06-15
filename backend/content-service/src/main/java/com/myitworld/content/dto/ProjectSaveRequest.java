package com.myitworld.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ProjectSaveRequest {

    @NotBlank
    @Size(max = 128)
    private String name;

    private String description;

    @Size(max = 512)
    private String coverUrl;

    @Size(max = 512)
    private String githubUrl;

    @Size(max = 512)
    private String demoUrl;

    private Integer featured = 0;
    private Integer sortOrder = 0;
    private Integer status = 1;
    private List<String> techStack;
}
