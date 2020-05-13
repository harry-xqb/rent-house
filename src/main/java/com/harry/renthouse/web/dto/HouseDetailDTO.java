package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *  房屋详情dto
 * @author Harry Xu
 * @date 2020/5/11 16:05
 */
@Data
public class HouseDetailDTO {

    @ApiModelProperty(value = "房屋详情id", example = "21")
    private Long id;

    @ApiModelProperty(value = "房屋描述", example = "这个人很懒，没有给房屋写描述")
    private String description;

    /* 户型介绍 */
    @ApiModelProperty(value = "户型介绍", example = "三室一厅一厨房带独卫")
    private String layoutDesc;

    /* 交通出行介绍 */
    @ApiModelProperty(value = "交通出行介绍", example = "交通便利，近萍水街地铁站")
    private String traffic;

    /* 周边配套设施 */
    @ApiModelProperty(value = "周边配套设施", example = "小区内游泳池，免费健身器材，距银泰百货不到500米")
    private String roundService;

    /* 出租方式, 1:整租  2: 合租 */
    @ApiModelProperty(value = "租房方式： 1: 整租 2: 合租", example = "1")
    private Integer rentWay;

    @ApiModelProperty(value = "房源地址", example = "拱墅区阮家桥公寓")
    private String address;

    /* 地铁线路id */
    @ApiModelProperty(value = "地铁线路id", example = "4")
    private Long subwayLineId;

    /* 地铁线路名称 */
    @ApiModelProperty(value = "地铁线路名称", example = "10号线")
    private String subwayLineName;

    @ApiModelProperty(value = "地铁站id", example = "21")
    private Long subwayStationId;

    @ApiModelProperty(value = "地铁站名称", example = "萍水街")
    private String subwayStationName;

    @ApiModelProperty(value = "房屋id", example = "29")
    private Long houseId;
}
