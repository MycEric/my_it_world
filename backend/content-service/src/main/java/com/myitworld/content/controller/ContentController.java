package com.myitworld.content.controller;

import com.myitworld.common.result.Result;
import com.myitworld.content.dto.*;
import com.myitworld.content.service.AboutService;
import com.myitworld.content.service.HomeService;
import com.myitworld.content.service.ProjectService;
import com.myitworld.content.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "内容-公开", description = "首页、关于我、技能、项目")
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final HomeService homeService;
    private final AboutService aboutService;
    private final SkillService skillService;
    private final ProjectService projectService;

    @Operation(summary = "首页聚合")
    @GetMapping("/home")
    public Result<HomeVO> home() {
        return Result.success(homeService.getHome());
    }

    @Operation(summary = "关于我")
    @GetMapping("/about")
    public Result<AboutVO> about() {
        return Result.success(aboutService.getAbout());
    }

    @Operation(summary = "技能列表（按分类）")
    @GetMapping("/skills")
    public Result<List<SkillCategoryVO>> skills() {
        return Result.success(skillService.listSkillsGrouped());
    }

    @Operation(summary = "项目列表（已展示）")
    @GetMapping("/projects")
    public Result<List<ProjectListItem>> projects() {
        return Result.success(projectService.listPublishedProjects());
    }

    @Operation(summary = "项目详情")
    @GetMapping("/projects/{id}")
    public Result<ProjectListItem> projectDetail(@PathVariable("id") Long id) {
        return Result.success(projectService.getProjectDetail(id));
    }
}
