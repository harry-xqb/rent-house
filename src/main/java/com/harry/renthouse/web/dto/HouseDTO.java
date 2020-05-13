package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  房源信息dto
 * @author Harry Xu
 * @date 2020/5/9 15:09
 */
@Data
public class HouseDTO {

    @ApiModelProperty(value = "房源id", example = "29")
    private Long id;

    @ApiModelProperty(value = "房源标题", example = "精品阮家桥公寓")
    private String title;

    @ApiModelProperty(value = "定价", example = "2600")
    private Integer price;

    @ApiModelProperty(value = "面积", example = "20")
    private Integer area;

    @ApiModelProperty(value = "房间数", example = "5")
    private Integer room;

    @ApiModelProperty(value = "楼层", example = "12")
    private Integer floor;

    @ApiModelProperty(value = "总楼层", example = "18")
    private Integer totalFloor;

    @ApiModelProperty(value = "被查看次数", example = "0")
    private Integer watchTimes;

    @ApiModelProperty(value = "建房年份", example = "2020")
    private Integer buildYear;

    @ApiModelProperty(value = "房原状态: 0:未审核  1:审核通过 2: 已出租 3:已删除", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建日期", example = "2020-05-12T01:04:56")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "上次更新日期", example = "2020-05-13T18:37:48")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "城市英文简称", example = "bj")
    private String cityEnName;

    @ApiModelProperty(value = "区县英文简称", example = "dcq")
    private String regionEnName;

    @ApiModelProperty(value = "房屋照片封面", example = "http://qa22ygxo8.bkt.clouddn.com/Fn6szUiUydhr3XE5xF55XCDvlc2E")
    private String cover;

    @ApiModelProperty(value = "房屋朝向: 1,2,3,4 -> 东南西北", example = "2")
    private Integer direction;

    /* 到地铁的距离 */
    @ApiModelProperty(value = "到地铁的距离, -1为附近无地铁", example = "500")
    private Integer distanceToSubway;

    /** 客厅数量 **/
    @ApiModelProperty(value = "客厅数量", example = "2")
    private Integer parlour;

    /* 小区名称 */
    @ApiModelProperty(value = "小区名称", example = "阮家桥公寓")
    private String district;

    /* 所属管理员id */
    @ApiModelProperty(value = "所属管理员id", example = "1")
    private Long adminId;

    @ApiModelProperty(value = "卫生间数量", example = "1")
    private Integer bathroom;

    /* 街道 */
    @ApiModelProperty(value = "街道", example = "祥符街道")
    private String street;

    /* 标签 */
    @ApiModelProperty(value = "标签", example = "['交通便利', '精装修']")
    private List<String> tags;

    /* 房屋照片集合 */
    @ApiModelProperty(value = "房屋照片集合", example = "[{'path': 'Fn6szUiUydhr3XE5xF55XCDvlc2E', 'width': 50}, 'height': 50]")
    private List<HousePictureDTO> housePictureList;

    /* 房屋信息 */
    @ApiModelProperty("房屋详细信息")
    private HouseDetailDTO houseDetail;

}
