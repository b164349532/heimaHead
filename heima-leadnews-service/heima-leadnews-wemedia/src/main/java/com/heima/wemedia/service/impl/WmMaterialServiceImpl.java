package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.common.wemedia.dtos.WmMaterialDto;
import com.heima.model.common.wemedia.pojos.WmMaterial;
import com.heima.utils.common.UploadUtil;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) throws Exception {
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);

        }

        String url = UploadUtil.upload(multipartFile);
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(url);
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setCreatedTime(new Date());
        boolean save = save(wmMaterial);
        System.out.println(save);
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult findList(WmMaterialDto wmMaterialDto) {
        //检查参数
        wmMaterialDto.checkParam();
        //分页查询
        IPage page = new Page(wmMaterialDto.getPage(),wmMaterialDto.getSize());
        LambdaQueryWrapper<WmMaterial> lqw = new LambdaQueryWrapper<>();
        if(wmMaterialDto.getIsCollection() != null && wmMaterialDto.getIsCollection() == 1){
            lqw.eq(WmMaterial::getIsCollection,wmMaterialDto.getIsCollection());
        }

        lqw.eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId());
        lqw.orderByDesc(WmMaterial::getCreatedTime);
        page = page(page,lqw);
        ResponseResult responseResult = new PageResponseResult(wmMaterialDto.getPage(),wmMaterialDto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
