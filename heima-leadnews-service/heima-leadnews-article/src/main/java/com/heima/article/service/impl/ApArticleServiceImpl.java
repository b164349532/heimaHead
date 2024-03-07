package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.common.article.dtos.ArticleHomeDto;
import com.heima.model.common.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;



@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    @Autowired
    private ApArticleMapper apArticleMapper;

    private final static short MAX_PAGE_SIZE = 50;


    /*
    1.加载更多
    2.加载更新
     */
    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        //1.参数校验
        //分页条数校验
        Integer size = dto.getSize();
        if(size == null || size == 0) {
            size = 10;
        }

        size = Math.min(size,MAX_PAGE_SIZE);

        //type校验
        if(!type.equals(ArticleConstants.LOAD_TYPE_MORE) || !type.equals(ArticleConstants.LOAD_TYPE_NEW)){
            type = ArticleConstants.LOAD_TYPE_MORE;
        }

        //频道参数
        if(StringUtils.isBlank(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null)dto.setMaxBehotTime(new Date());
        if(dto.getMaxBehotTime() == null)dto.setMinBehotTime(new Date());


        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, type);
        return ResponseResult.okResult(apArticles);
    }
}
