package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.common.article.dtos.ArticleHomeDto;
import com.heima.model.common.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /*
    加载文章列表
     */

    public List<ApArticle> loadArticleList(ArticleHomeDto dto, Short type);
}
