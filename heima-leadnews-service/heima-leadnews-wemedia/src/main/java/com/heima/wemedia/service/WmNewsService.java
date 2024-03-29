package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.wemedia.dtos.WmNewsDto;
import com.heima.model.common.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.common.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {
    ResponseResult findList(WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto wmNewsDto);
}
