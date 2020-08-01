package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Harry Xu
 * @date 2020/7/29 13:58
 */
@Data
@ApiModel("用户对房源的操作 DTO")
public class UserHouseOperateDTO {

    @ApiModelProperty(value = "是否收藏")
    private boolean star;

    @ApiModelProperty(value = "是否预约")
    private boolean reserve;
}
