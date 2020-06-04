package com.harry.renthouse.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author Harry Xu
 * @date 2020/5/12 16:46
 */
@Data
@ApiModel("房屋完整信息")
public class HouseCompleteInfoDTO {

    @ApiModelProperty(value = "房屋信息")
    private HouseDTO house;

    @ApiModelProperty(value = "城市信息")
    private SupportAddressDTO city;

    @ApiModelProperty(value = "区县信息")
    private SupportAddressDTO region;

    @ApiModelProperty(value = "地铁线路信息")
    private SubwayDTO subway;

    @ApiModelProperty(value = "地铁站信息")
    private SubwayStationDTO subwayStation;


    @ApiModelProperty(value = "房屋预约状态")
    private int houseSubscribeStatus;

}
