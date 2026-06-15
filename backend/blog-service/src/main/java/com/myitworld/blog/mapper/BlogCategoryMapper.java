package com.myitworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myitworld.blog.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {

    /** 查询某篇文章关联的分类列表 */
    @Select("""
            SELECT c.* FROM blog_category c
            INNER JOIN blog_article_category ac ON c.id = ac.category_id
            WHERE ac.article_id = #{articleId}
            ORDER BY c.sort_order ASC
            """)
    List<BlogCategory> selectByArticleId(@Param("articleId") Long articleId);
}
