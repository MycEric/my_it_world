package com.myitworld.content.service;

import com.myitworld.common.result.Result;
import com.myitworld.content.client.BlogFeignClient;
import com.myitworld.content.dto.BlogArticleBrief;
import com.myitworld.content.dto.HomeVO;
import com.myitworld.content.dto.ProjectListItem;
import com.myitworld.content.dto.SkillItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final ContentCacheService cacheService;
    private final AboutService aboutService;
    private final SkillService skillService;
    private final ProjectService projectService;
    private final BlogFeignClient blogFeignClient;

    public HomeVO getHome() {
        return cacheService.getOrLoad("home", HomeVO.class, this::buildHome);
    }

    private HomeVO buildHome() {
        HomeVO home = new HomeVO();
        home.setAbout(aboutService.getAbout());
        home.setFeaturedSkills(skillService.listFeaturedSkills(12));
        home.setFeaturedProjects(projectService.listFeaturedProjects(6));
        home.setLatestBlogs(fetchLatestBlogs(5));
        return home;
    }

    private List<BlogArticleBrief> fetchLatestBlogs(int limit) {
        try {
            Result<List<BlogArticleBrief>> result = blogFeignClient.latest(limit);
            if (result != null && result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
        } catch (Exception e) {
            log.warn("调用 blog-service 获取最新文章失败: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
