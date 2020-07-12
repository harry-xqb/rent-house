package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  支持区域dto
 * @author Harry Xu
 * @date 2020/5/8 17:36
 */
@Data
@ApiModel("支持的区域 DTO")
public class SupportAddressDTO {

    @ApiModelProperty(value = "地区id", example = "4")
    private Long id;

    @ApiModelProperty(value = "英文缩写", example = "bj")
    private String enName;

    @ApiModelProperty(value = "中文名称", example = "北京")
    private String cnName;

    @ApiModelProperty(value = "地区等级: city:城市 region: 区县", example = "city", allowableValues = "city,region")
    private String level;

    @ApiModelProperty(value = "地区行政代码", example = "bg")
    private String code;

    @ApiModelProperty(value = "百度地图经度")
    private double baiduMapLng;

    @ApiModelProperty(value = "百度地图纬度")
    private double baiduMapLat;


}
