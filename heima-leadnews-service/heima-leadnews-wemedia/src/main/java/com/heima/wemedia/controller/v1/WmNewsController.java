package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.wemedia.dtos.WmNewsDto;
import com.heima.model.common.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.common.wemedia.pojos.WmNews;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    WmNewsService wmNewsService;
    @PostMapping("/list")
    public ResponseResult findList(@RequestBody WmNewsPageReqDto dto) {


        return wmNewsService.findList(dto);
    }


    @PostMapping("/submit")
    public ResponseResult saveNews(@RequestBody WmNewsDto wmNewsDto){

        return wmNewsService.submitNews(wmNewsDto);
    }
}
