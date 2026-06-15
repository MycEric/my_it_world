package com.myitworld.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import com.myitworld.content.dto.ProjectListItem;
import com.myitworld.content.dto.ProjectSaveRequest;
import com.myitworld.content.entity.Project;
import com.myitworld.content.entity.ProjectTechStack;
import com.myitworld.content.mapper.ProjectMapper;
import com.myitworld.content.mapper.ProjectTechStackMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectMapper projectMapper;
    private final ProjectTechStackMapper techStackMapper;
    private final ContentCacheService cacheService;

    public List<ProjectListItem> listPublishedProjects() {
        return cacheService.getOrLoad("projects", new TypeReference<List<ProjectListItem>>() {}, () -> loadProjects(1));
    }

    public List<ProjectListItem> listAllProjectsAdmin() {
        return loadProjects(null);
    }

    public ProjectListItem getProjectDetail(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null || project.getStatus() == null || project.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在或未展示");
        }
        return toListItem(project, loadTechStack(id));
    }

    public ProjectListItem getProjectAdmin(Long id) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }
        return toListItem(project, loadTechStack(id));
    }

    public List<ProjectListItem> listFeaturedProjects(int limit) {
        List<Project> projects = projectMapper.selectList(
                new LambdaQueryWrapper<Project>()
                        .eq(Project::getStatus, 1)
                        .eq(Project::getFeatured, 1)
                        .orderByAsc(Project::getSortOrder)
                        .last("LIMIT " + limit));
        return toListItems(projects);
    }

    @Transactional
    public Long createProject(ProjectSaveRequest request) {
        Project project = new Project();
        applyProject(project, request);
        projectMapper.insert(project);
        saveTechStack(project.getId(), request.getTechStack());
        cacheService.evictAll();
        return project.getId();
    }

    @Transactional
    public void updateProject(Long id, ProjectSaveRequest request) {
        Project project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "项目不存在");
        }
        applyProject(project, request);
        projectMapper.updateById(project);
        techStackMapper.delete(new LambdaQueryWrapper<ProjectTechStack>().eq(ProjectTechStack::getProjectId, id));
        saveTechStack(id, request.getTechStack());
        cacheService.evictAll();
    }

    @Transactional
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
        techStackMapper.delete(new LambdaQueryWrapper<ProjectTechStack>().eq(ProjectTechStack::getProjectId, id));
        cacheService.evictAll();
    }

    private List<ProjectListItem> loadProjects(Integer status) {
        LambdaQueryWrapper<Project> qw = new LambdaQueryWrapper<Project>()
                .orderByAsc(Project::getSortOrder)
                .orderByDesc(Project::getId);
        if (status != null) {
            qw.eq(Project::getStatus, status);
        }
        return toListItems(projectMapper.selectList(qw));
    }

    private List<ProjectListItem> toListItems(List<Project> projects) {
        if (projects.isEmpty()) {
            return List.of();
        }
        List<Long> ids = projects.stream().map(Project::getId).toList();
        Map<Long, List<String>> techMap = loadTechStacks(ids);
        return projects.stream()
                .map(p -> toListItem(p, techMap.getOrDefault(p.getId(), List.of())))
                .toList();
    }

    private Map<Long, List<String>> loadTechStacks(List<Long> projectIds) {
        List<ProjectTechStack> stacks = techStackMapper.selectList(
                new LambdaQueryWrapper<ProjectTechStack>()
                        .in(ProjectTechStack::getProjectId, projectIds)
                        .orderByAsc(ProjectTechStack::getSortOrder));
        return stacks.stream().collect(Collectors.groupingBy(
                ProjectTechStack::getProjectId,
                Collectors.mapping(ProjectTechStack::getTechName, Collectors.toList())));
    }

    private List<String> loadTechStack(Long projectId) {
        return techStackMapper.selectList(
                new LambdaQueryWrapper<ProjectTechStack>()
                        .eq(ProjectTechStack::getProjectId, projectId)
                        .orderByAsc(ProjectTechStack::getSortOrder))
                .stream().map(ProjectTechStack::getTechName).toList();
    }

    private void saveTechStack(Long projectId, List<String> techStack) {
        if (techStack == null || techStack.isEmpty()) {
            return;
        }
        int order = 0;
        for (String tech : techStack) {
            if (tech == null || tech.isBlank()) {
                continue;
            }
            ProjectTechStack row = new ProjectTechStack();
            row.setProjectId(projectId);
            row.setTechName(tech.trim());
            row.setSortOrder(order++);
            techStackMapper.insert(row);
        }
    }

    private void applyProject(Project project, ProjectSaveRequest request) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCoverUrl(request.getCoverUrl());
        project.setGithubUrl(request.getGithubUrl());
        project.setDemoUrl(request.getDemoUrl());
        project.setFeatured(request.getFeatured() != null ? request.getFeatured() : 0);
        project.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        project.setStatus(request.getStatus() != null ? request.getStatus() : 1);
    }

    private ProjectListItem toListItem(Project project, List<String> techStack) {
        ProjectListItem item = new ProjectListItem();
        item.setId(project.getId());
        item.setName(project.getName());
        item.setDescription(project.getDescription());
        item.setCoverUrl(project.getCoverUrl());
        item.setGithubUrl(project.getGithubUrl());
        item.setDemoUrl(project.getDemoUrl());
        item.setFeatured(project.getFeatured());
        item.setSortOrder(project.getSortOrder());
        item.setStatus(project.getStatus());
        item.setTechStack(techStack != null ? techStack : new ArrayList<>());
        item.setUpdateTime(project.getUpdateTime());
        return item;
    }
}
