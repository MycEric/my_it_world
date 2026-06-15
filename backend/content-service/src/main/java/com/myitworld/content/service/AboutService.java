package com.myitworld.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myitworld.content.dto.AboutSaveRequest;
import com.myitworld.content.dto.AboutVO;
import com.myitworld.content.entity.AboutInfo;
import com.myitworld.content.mapper.AboutInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutService {

    private static final long ABOUT_ID = 1L;

    private final AboutInfoMapper aboutInfoMapper;
    private final ContentCacheService cacheService;

    public AboutVO getAbout() {
        return cacheService.getOrLoad("about", AboutVO.class, this::loadAbout);
    }

    private AboutVO loadAbout() {
        AboutInfo entity = aboutInfoMapper.selectById(ABOUT_ID);
        if (entity == null) {
            return null;
        }
        return toVO(entity);
    }

    @Transactional
    public AboutVO updateAbout(AboutSaveRequest request) {
        AboutInfo existing = aboutInfoMapper.selectById(ABOUT_ID);
        AboutInfo entity = existing != null ? existing : new AboutInfo();
        entity.setId(ABOUT_ID);
        entity.setSlogan(request.getSlogan());
        entity.setSummary(request.getSummary());
        entity.setContent(request.getContent());
        entity.setAvatarUrl(request.getAvatarUrl());
        entity.setEmail(request.getEmail());
        entity.setLocation(request.getLocation());
        entity.setGithubUrl(request.getGithubUrl());
        entity.setCsdnUrl(request.getCsdnUrl());
        entity.setLinkedinUrl(request.getLinkedinUrl());
        if (existing == null) {
            aboutInfoMapper.insert(entity);
        } else {
            aboutInfoMapper.updateById(entity);
        }
        cacheService.evictAll();
        return toVO(aboutInfoMapper.selectById(ABOUT_ID));
    }

    AboutVO toVO(AboutInfo entity) {
        AboutVO vo = new AboutVO();
        vo.setId(entity.getId());
        vo.setSlogan(entity.getSlogan());
        vo.setSummary(entity.getSummary());
        vo.setContent(entity.getContent());
        vo.setAvatarUrl(entity.getAvatarUrl());
        vo.setEmail(entity.getEmail());
        vo.setLocation(entity.getLocation());
        vo.setGithubUrl(entity.getGithubUrl());
        vo.setCsdnUrl(entity.getCsdnUrl());
        vo.setLinkedinUrl(entity.getLinkedinUrl());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
