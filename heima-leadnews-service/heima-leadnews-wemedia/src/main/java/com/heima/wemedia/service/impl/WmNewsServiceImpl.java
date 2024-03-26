package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.common.wemedia.dtos.WmNewsDto;
import com.heima.model.common.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.common.wemedia.pojos.WmMaterial;
import com.heima.model.common.wemedia.pojos.WmNews;
import com.heima.model.common.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.heima.common.constants.WemediaConstants.*;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Override
    public ResponseResult findList(WmNewsPageReqDto dto) {
        //分页检查
        dto.checkParam();
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmNews> qw = new LambdaQueryWrapper<>();
        //状态查询
        if(dto.getStatus() != null) {
            qw.eq(WmNews::getStatus,dto.getStatus());
        }
        //频道精准查询
        if(dto.getChannelId() != null) {
            qw.eq(WmNews::getChannelId,dto.getChannelId());
        }

        //时间范围查询
        if(dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            qw.between(WmNews::getPublishTime,dto.getBeginPubDate(),dto.getEndPubDate());

        }

        //关键字模糊查询
        if(StringUtils.isNotBlank(dto.getKeyword())) {
            qw.like(WmNews::getTitle, dto.getKeyword());
        }
        //查询当前登录人的文章
        qw.eq(WmNews::getUserId, WmThreadLocalUtil.getUser().getId());
        //按照发布时间倒叙查询
        qw.orderByDesc(WmNews::getCreatedTime);
        page(page,qw);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    @Override
    public ResponseResult submitNews(WmNewsDto wmNewsDto) {
        if(wmNewsDto == null || wmNewsDto.getContent() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews wmNews = new WmNews();
        //属性拷贝,属性名称和类型相同 才能拷贝
        BeanUtils.copyProperties(wmNewsDto,wmNews);
        //图片的collect 转变为 list
        if(wmNewsDto != null && wmNewsDto.getImages().size() > 0){
            String join = StringUtils.join(wmNewsDto.getImages(), ",");
            System.out.println(join);
            wmNews.setImages(join);
        }

        //传过来一个-1 进行判断
        if(wmNewsDto.getType().equals(WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }

        saveOrUpdateWmNews(wmNews);
        //如果为草稿
        if(wmNewsDto.getStatus() == 0){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        //抽取图片
        List<String> materialUrl = extractUrlInfo(wmNewsDto.getContent());
        //不是草稿
        saveRelationInfoForContent(materialUrl, wmNews.getId());
        //不是草稿去匹配素材图片
        saveRelativeInfoForCover(wmNewsDto,wmNews,materialUrl);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }




    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    //保存或者更新
    private void saveOrUpdateWmNews(WmNews wmNews) {
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1); //默认上架
        if(wmNews.getId() == null){
            save(wmNews);
        }else {
            LambdaQueryWrapper<WmNewsMaterial> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WmNewsMaterial::getNewsId,wmNews.getId());
            wmNewsMaterialMapper.delete(wrapper);

            updateById(wmNews);
        }



    }

    /**
     *提取文章内容中的图片
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);

        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                String imageValue = (String) map.get("value");
                materials.add(imageValue);
            }
        }
        return materials;
    }


    private void saveRelationInfoForContent(List<String> materialUrl, Integer newsId) {
        saveRelativeInfo(materialUrl,newsId, WemediaConstants.WM_CONTENT_REFERENCE);
    }

    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    private void saveRelativeInfo(List<String> materialUrl, Integer newsId, Short wmContentReference) {
       if(materialUrl != null && !materialUrl.isEmpty()){
           LambdaQueryWrapper <WmMaterial> wrapper = new LambdaQueryWrapper();
           wrapper.in(WmMaterial::getUrl, materialUrl);
           List<WmMaterial> wmMaterials = wmMaterialMapper.selectList(wrapper);
           List<Integer> idList = wmMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

           wmNewsMaterialMapper.saveRelations(idList,newsId,wmContentReference);
       }


    }

    /**
     *
     * 匹配规则 1 3, 1
     *        >= 3, 3
     *        0,0
     * 保存封面图片与素材的关系
     * @param wmNewsDto
     * @param wmNews
     * @param materialUrl
     */
    private void saveRelativeInfoForCover(WmNewsDto wmNewsDto, WmNews wmNews, List<String> materialUrl) {
        if(wmNewsDto.getType().equals(WM_NEWS_TYPE_AUTO)){
            List<String> images = wmNewsDto.getImages();
            //多图
            if(materialUrl.size() >= 3){
                wmNews.setType(WM_NEWS_MANY_IMAGE);

                images = materialUrl.stream().limit(3).collect(Collectors.toList());
            }else  if(materialUrl.size() >= 1 && materialUrl.size() < 3){  //单图
                wmNews.setType(WM_NEWS_SINGLE_IMAGE);
                images = materialUrl.stream().limit(1).collect(Collectors.toList());
            }else {
                wmNews.setType(WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images.size() > 0 && images != null){
                String imagesString = StringUtils.join(images, ",");
                wmNews.setImages(imagesString);
            }

            updateById(wmNews);

            if(images.size() > 0 && images != null){
                saveRelativeInfo(images, wmNews.getId(), WM_COVER_REFERENCE);
            }
        }
    }
}
