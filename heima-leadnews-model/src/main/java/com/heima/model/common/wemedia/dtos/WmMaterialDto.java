package com.heima.model.common.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialDto extends PageRequestDto {

   /*
   是否收藏
    */
    private Short isCollection;

}
