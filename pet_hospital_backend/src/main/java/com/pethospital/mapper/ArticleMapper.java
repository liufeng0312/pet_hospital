package com.pethospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询已发布文章列表
     */
    @Select("<script>" +
            "SELECT * FROM articles " +
            "WHERE deleted_at IS NULL " +
            "AND is_published = 1 " +
            "<if test='category != null and category != \"\"'> AND category = #{category} </if>" +
            "ORDER BY published_at DESC" +
            "</script>")
    List<Article> selectPublishedPage(Page<Article> page, @Param("category") String category);

    /**
     * 增加浏览次数
     */
    @Update("UPDATE articles SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
}
