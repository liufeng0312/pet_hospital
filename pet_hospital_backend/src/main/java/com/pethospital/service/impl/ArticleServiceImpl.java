package com.pethospital.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pethospital.entity.Article;
import com.pethospital.mapper.ArticleMapper;
import com.pethospital.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> 
        implements ArticleService {

    private final ArticleMapper articleMapper;

    @Override
    public Page<Article> listArticles(String category, Integer page, Integer size) {
        Page<Article> pageParam = new Page<>(page, size);
        List<Article> records = articleMapper.selectPublishedPage(pageParam, category);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public Page<Article> manageList(String keyword, String category, Integer page, Integer size) {
        Page<Article> pageParam = new Page<>(page, size);
        return page(pageParam, new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Article>()
                .like(keyword != null && !keyword.isEmpty(), Article::getTitle, keyword)
                .eq(category != null && !category.isEmpty(), Article::getCategory, category)
                .orderByDesc(Article::getCreatedAt));
    }

    @Override
    @Transactional
    public Article getDetail(Long id) {
        Article article = getById(id);
        if (article != null) {
            // 增加浏览次数
            articleMapper.incrementViewCount(id);
        }
        return article;
    }

    @Override
    @Transactional
    public Article createArticle(Article article) {
        if (article.getIsPublished() == 1) {
            article.setPublishedAt(LocalDateTime.now());
        }
        save(article);
        return article;
    }

    @Override
    @Transactional
    public boolean updateArticle(Article article) {
        Article existing = getById(article.getId());
        if (existing != null && existing.getIsPublished() == 0 && article.getIsPublished() == 1) {
            // 从草稿变为发布，设置发布时间
            article.setPublishedAt(LocalDateTime.now());
        }
        return updateById(article);
    }
}
