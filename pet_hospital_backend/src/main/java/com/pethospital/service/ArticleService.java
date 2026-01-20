package com.pethospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pethospital.entity.Article;

public interface ArticleService extends IService<Article> {
    
    /**
     * 获取文章列表（已发布）
     */
    Page<Article> listArticles(String category, Integer page, Integer size);

    /**
     * 管理后台获取文章列表（所有状态，支持搜索）
     */
    Page<Article> manageList(String keyword, String category, Integer page, Integer size);
    
    /**
     * 获取文章详情（自动增加浏览次数）
     */
    Article getDetail(Long id);
    
    /**
     * 创建文章
     */
    Article createArticle(Article article);
    
    /**
     * 更新文章
     */
    boolean updateArticle(Article article);
}
