package com.pethospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pethospital.common.Result;
import com.pethospital.entity.Article;
import com.pethospital.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "健康资讯管理")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @ApiOperation("获取文章列表")
    @GetMapping("/list")
    public Result<Page<Article>> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(articleService.listArticles(category, page, size));
    }

    @ApiOperation("获取文章详情")
    @GetMapping("/{id}")
    public Result<Article> getDetail(@PathVariable Long id) {
        return Result.success(articleService.getDetail(id));
    }

    @ApiOperation("创建文章（管理端）")
    @PostMapping
    public Result<Article> create(@RequestBody Article article) {
        return Result.success(articleService.createArticle(article));
    }

    @ApiOperation("更新文章（管理端）")
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        return Result.success(articleService.updateArticle(article));
    }

    @ApiOperation("管理端获取文章列表")
    @GetMapping("/admin/list")
    public Result<Page<Article>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(articleService.manageList(keyword, category, page, size));
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(articleService.removeById(id));
    }
}
