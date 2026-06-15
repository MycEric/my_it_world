package com.myitworld.content.dto;

import lombok.Data;

import java.util.List;

@Data
public class HomeVO {

    private AboutVO about;
    private List<SkillItemVO> featuredSkills;
    private List<ProjectListItem> featuredProjects;
    private List<BlogArticleBrief> latestBlogs;
}
