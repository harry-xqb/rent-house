package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/6/1 10:56
 */
@Data
@ApiModel("房屋地图聚合结果")
public class HouseMapRegionsAggDTO {

    @ApiModelProperty(value = "聚合结果")
    private List<HouseBucketDTO> aggData;

    @ApiModelProperty(value = "房源总数")
    private long totalHouse;

    @ApiModelProperty(value = "区县集合")
    private List<SupportAddressDTO> regions;
}
