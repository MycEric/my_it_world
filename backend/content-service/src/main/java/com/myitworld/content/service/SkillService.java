package com.myitworld.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import com.myitworld.content.dto.SkillCategorySaveRequest;
import com.myitworld.content.dto.SkillCategoryVO;
import com.myitworld.content.dto.SkillItemSaveRequest;
import com.myitworld.content.dto.SkillItemVO;
import com.myitworld.content.entity.SkillCategory;
import com.myitworld.content.entity.SkillItem;
import com.myitworld.content.mapper.SkillCategoryMapper;
import com.myitworld.content.mapper.SkillItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillCategoryMapper categoryMapper;
    private final SkillItemMapper itemMapper;
    private final ContentCacheService cacheService;

    public List<SkillCategoryVO> listSkillsGrouped() {
        return cacheService.getOrLoad("skills", new TypeReference<List<SkillCategoryVO>>() {}, this::loadSkillsGrouped);
    }

    private List<SkillCategoryVO> loadSkillsGrouped() {
        List<SkillCategory> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<SkillCategory>()
                        .orderByAsc(SkillCategory::getSortOrder)
                        .orderByAsc(SkillCategory::getId));
        List<SkillItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<SkillItem>()
                        .orderByAsc(SkillItem::getSortOrder)
                        .orderByAsc(SkillItem::getId));
        Map<Long, List<SkillItem>> byCategory = items.stream()
                .collect(Collectors.groupingBy(SkillItem::getCategoryId));

        List<SkillCategoryVO> result = new ArrayList<>();
        for (SkillCategory cat : categories) {
            SkillCategoryVO vo = new SkillCategoryVO();
            vo.setId(cat.getId());
            vo.setName(cat.getName());
            vo.setSortOrder(cat.getSortOrder());
            List<SkillItemVO> itemVOs = byCategory.getOrDefault(cat.getId(), List.of())
                    .stream().map(this::toItemVO).toList();
            vo.setItems(itemVOs);
            result.add(vo);
        }
        return result;
    }

    public List<SkillItemVO> listFeaturedSkills(int limit) {
        List<SkillItem> items = itemMapper.selectList(
                new LambdaQueryWrapper<SkillItem>()
                        .eq(SkillItem::getFeatured, 1)
                        .orderByAsc(SkillItem::getSortOrder)
                        .last("LIMIT " + limit));
        return items.stream().map(this::toItemVO).toList();
    }

    public List<SkillCategory> listCategoriesAdmin() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<SkillCategory>()
                        .orderByAsc(SkillCategory::getSortOrder));
    }

    public List<SkillItemVO> listItemsAdmin(Long categoryId) {
        LambdaQueryWrapper<SkillItem> qw = new LambdaQueryWrapper<SkillItem>()
                .orderByAsc(SkillItem::getSortOrder);
        if (categoryId != null) {
            qw.eq(SkillItem::getCategoryId, categoryId);
        }
        return itemMapper.selectList(qw).stream().map(this::toItemVO).toList();
    }

    @Transactional
    public Long createCategory(SkillCategorySaveRequest request) {
        SkillCategory cat = new SkillCategory();
        cat.setName(request.getName());
        cat.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        categoryMapper.insert(cat);
        cacheService.evictAll();
        return cat.getId();
    }

    @Transactional
    public void updateCategory(Long id, SkillCategorySaveRequest request) {
        SkillCategory cat = categoryMapper.selectById(id);
        if (cat == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        cat.setName(request.getName());
        if (request.getSortOrder() != null) {
            cat.setSortOrder(request.getSortOrder());
        }
        categoryMapper.updateById(cat);
        cacheService.evictAll();
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
        itemMapper.delete(new LambdaQueryWrapper<SkillItem>().eq(SkillItem::getCategoryId, id));
        cacheService.evictAll();
    }

    @Transactional
    public Long createItem(SkillItemSaveRequest request) {
        SkillItem item = new SkillItem();
        applyItem(item, request);
        itemMapper.insert(item);
        cacheService.evictAll();
        return item.getId();
    }

    @Transactional
    public void updateItem(Long id, SkillItemSaveRequest request) {
        SkillItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "技能不存在");
        }
        applyItem(item, request);
        itemMapper.updateById(item);
        cacheService.evictAll();
    }

    @Transactional
    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
        cacheService.evictAll();
    }

    private void applyItem(SkillItem item, SkillItemSaveRequest request) {
        item.setCategoryId(request.getCategoryId());
        item.setName(request.getName());
        item.setLevel(request.getLevel() != null ? request.getLevel() : 3);
        item.setIconUrl(request.getIconUrl());
        item.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        item.setFeatured(request.getFeatured() != null ? request.getFeatured() : 0);
    }

    private SkillItemVO toItemVO(SkillItem item) {
        SkillItemVO vo = new SkillItemVO();
        vo.setId(item.getId());
        vo.setCategoryId(item.getCategoryId());
        vo.setName(item.getName());
        vo.setLevel(item.getLevel());
        vo.setIconUrl(item.getIconUrl());
        vo.setSortOrder(item.getSortOrder());
        vo.setFeatured(item.getFeatured());
        return vo;
    }
}
