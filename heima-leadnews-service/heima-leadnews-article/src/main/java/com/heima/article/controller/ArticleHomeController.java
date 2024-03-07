package com.heima.article.controller;


import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.common.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {
    @Autowired
    private ApArticleService apArticleService;

    /*
    加载首页
     */
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto articleHomeDto) {

        return apArticleService.load(articleHomeDto, ArticleConstants.LOAD_TYPE_MORE);
    }

    /*
    加载更多
     */
    @PostMapping("/loadmore")
    public ResponseResult loadmore(@RequestBody ArticleHomeDto articleHomeDto) {

        return apArticleService.load(articleHomeDto, ArticleConstants.LOAD_TYPE_MORE);
    }

    /*
    加载最新
     */
    @PostMapping("/loadnew")
    public ResponseResult loadnew(@RequestBody ArticleHomeDto articleHomeDto) {

        return apArticleService.load(articleHomeDto, ArticleConstants.LOAD_TYPE_NEW);
    }
}
