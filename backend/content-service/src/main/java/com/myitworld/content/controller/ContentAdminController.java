package com.myitworld.content.controller;

import com.myitworld.common.result.Result;
import com.myitworld.content.dto.*;
import com.myitworld.content.entity.SkillCategory;
import com.myitworld.content.service.AboutService;
import com.myitworld.content.service.ProjectService;
import com.myitworld.content.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "内容-管理", description = "关于我、技能、项目 CRUD")
@RestController
@RequestMapping("/api/content/admin")
@RequiredArgsConstructor
public class ContentAdminController {

    private final AboutService aboutService;
    private final SkillService skillService;
    private final ProjectService projectService;

    @Operation(summary = "获取关于我")
    @GetMapping("/about")
    public Result<AboutVO> getAbout() {
        return Result.success(aboutService.getAbout());
    }

    @Operation(summary = "更新关于我")
    @PutMapping("/about")
    public Result<AboutVO> updateAbout(@Valid @RequestBody AboutSaveRequest request) {
        return Result.success(aboutService.updateAbout(request));
    }

    @Operation(summary = "技能分类列表")
    @GetMapping("/skill-categories")
    public Result<List<SkillCategory>> listCategories() {
        return Result.success(skillService.listCategoriesAdmin());
    }

    @Operation(summary = "新建技能分类")
    @PostMapping("/skill-categories")
    public Result<Map<String, Long>> createCategory(@Valid @RequestBody SkillCategorySaveRequest request) {
        return Result.success(Map.of("id", skillService.createCategory(request)));
    }

    @Operation(summary = "更新技能分类")
    @PutMapping("/skill-categories/{id}")
    public Result<Void> updateCategory(@PathVariable("id") Long id,
                                       @Valid @RequestBody SkillCategorySaveRequest request) {
        skillService.updateCategory(id, request);
        return Result.success();
    }

    @Operation(summary = "删除技能分类")
    @DeleteMapping("/skill-categories/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        skillService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "技能项列表")
    @GetMapping("/skill-items")
    public Result<List<SkillItemVO>> listItems(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        return Result.success(skillService.listItemsAdmin(categoryId));
    }

    @Operation(summary = "新建技能项")
    @PostMapping("/skill-items")
    public Result<Map<String, Long>> createItem(@Valid @RequestBody SkillItemSaveRequest request) {
        return Result.success(Map.of("id", skillService.createItem(request)));
    }

    @Operation(summary = "更新技能项")
    @PutMapping("/skill-items/{id}")
    public Result<Void> updateItem(@PathVariable("id") Long id,
                                   @Valid @RequestBody SkillItemSaveRequest request) {
        skillService.updateItem(id, request);
        return Result.success();
    }

    @Operation(summary = "删除技能项")
    @DeleteMapping("/skill-items/{id}")
    public Result<Void> deleteItem(@PathVariable("id") Long id) {
        skillService.deleteItem(id);
        return Result.success();
    }

    @Operation(summary = "项目列表（管理端）")
    @GetMapping("/projects")
    public Result<List<ProjectListItem>> listProjects() {
        return Result.success(projectService.listAllProjectsAdmin());
    }

    @Operation(summary = "项目详情（管理端）")
    @GetMapping("/projects/{id}")
    public Result<ProjectListItem> getProject(@PathVariable("id") Long id) {
        return Result.success(projectService.getProjectAdmin(id));
    }

    @Operation(summary = "新建项目")
    @PostMapping("/projects")
    public Result<Map<String, Long>> createProject(@Valid @RequestBody ProjectSaveRequest request) {
        return Result.success(Map.of("id", projectService.createProject(request)));
    }

    @Operation(summary = "更新项目")
    @PutMapping("/projects/{id}")
    public Result<Void> updateProject(@PathVariable("id") Long id,
                                      @Valid @RequestBody ProjectSaveRequest request) {
        projectService.updateProject(id, request);
        return Result.success();
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/projects/{id}")
    public Result<Void> deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}
