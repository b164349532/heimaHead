package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.article.dtos.ArticleHomeDto;
import com.heima.model.common.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;

public interface ApArticleService extends IService<ApArticle> {
    public ResponseResult load(ArticleHomeDto dto, Short type);
}
