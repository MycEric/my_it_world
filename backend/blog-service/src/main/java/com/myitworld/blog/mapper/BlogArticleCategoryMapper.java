package com.myitworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myitworld.blog.entity.BlogArticleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BlogArticleCategoryMapper extends BaseMapper<BlogArticleCategory> {

    /** 更新文章分类时，先删除旧关联 */
    @Delete("DELETE FROM blog_article_category WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
}
