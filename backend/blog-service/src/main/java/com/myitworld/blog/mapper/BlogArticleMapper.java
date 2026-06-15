package com.myitworld.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.myitworld.blog.entity.BlogArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 博客文章 Mapper
 */
@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {

    /**
     * 公开分页查询：仅已发布文章，支持标题关键字模糊搜索
     */
    @Select("""
            <script>
            SELECT DISTINCT a.*
            FROM blog_article a
            <if test="categoryId != null">
                INNER JOIN blog_article_category ac ON a.id = ac.article_id
            </if>
            WHERE a.deleted = 0 AND a.status = 1
            <if test="keyword != null and keyword != ''">
                AND (a.title LIKE CONCAT('%', #{keyword}, '%')
                     OR a.summary LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="categoryId != null">
                AND ac.category_id = #{categoryId}
            </if>
            ORDER BY a.publish_time DESC, a.id DESC
            </script>
            """)
    IPage<BlogArticle> selectPublishedPage(Page<BlogArticle> page,
                                           @Param("keyword") String keyword,
                                           @Param("categoryId") Long categoryId);

    /**
     * 管理端分页：含草稿/下架，可按状态筛选
     */
    @Select("""
            <script>
            SELECT a.* FROM blog_article a
            WHERE a.deleted = 0
            <if test="status != null">
                AND a.status = #{status}
            </if>
            <if test="keyword != null and keyword != ''">
                AND a.title LIKE CONCAT('%', #{keyword}, '%')
            </if>
            ORDER BY a.update_time DESC
            </script>
            """)
    IPage<BlogArticle> selectAdminPage(Page<BlogArticle> page,
                                       @Param("keyword") String keyword,
                                       @Param("status") Integer status);

    /** 浏览量 +1 */
    @Update("UPDATE blog_article SET view_count = view_count + 1 WHERE id = #{id} AND deleted = 0")
    int incrementViewCount(@Param("id") Long id);

    /** 查询最新已发布文章 */
    @Select("""
            SELECT * FROM blog_article
            WHERE deleted = 0 AND status = 1
            ORDER BY publish_time DESC, id DESC
            LIMIT #{limit}
            """)
    List<BlogArticle> selectLatestPublished(@Param("limit") int limit);
}
