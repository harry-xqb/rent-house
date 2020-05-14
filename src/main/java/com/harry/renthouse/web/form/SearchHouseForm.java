package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 房源搜索表单
 * @author Harry Xu
 * @date 2020/5/14 10:20
 */
@Data
public class SearchHouseForm {

    @NotNull(message = "城市英文简称不能为空")
    @ApiModelProperty(value = "城市英文缩写", required = true ,example = "bj")
    private String cityEnName;

    @ApiModelProperty(value = "区县英文缩写", example = "hdq")
    private String regionEnName;

    @ApiModelProperty(value = "地铁线路id")
    private Long subwayLineId;

    @ApiModelProperty(value = "地铁站id")
    private Long subwayStationId;

    @ApiModelProperty(value = "出租方式(0: 合租 1:整租)", allowableValues = "0, 1", example = "0")
    private Integer rentWay = -1;

    @ApiModelProperty(value = "价格区间最小值", example = "0")
    @Min(value = 0, message = "最低价格不能小于0")
    private Integer priceMin;

    @ApiModelProperty(value = "价格区间最大值", example = "10000")
    @Min(value = 0, message = "最高价格不能小于0")
    private Integer priceMax;

    @ApiModelProperty(value = "房源标签", example = "['独立卫生间']")
    private List<String> tags;

    @ApiModelProperty(value = "房屋朝向:1,2,3,4 -> 东南西北", example = "1")
    private Integer direction;

    @ApiModelProperty(value = "页号, 最小为1", example = "1")
    @Min(value = 1, message = "页号大小不能小于1")
    private Integer page = 1;

    @ApiModelProperty(value = "页面大小", example = "15")
    @Min(value = 0, message = "页面数量大小不能小于0")
    private Integer pageSize = 15;

    @ApiModelProperty(value = "排序方式", allowableValues = "lastUpdateTime,price,area", example = "lastUpdateTime")
    private String orderBy = "lastUpdateTime";

    @ApiModelProperty(value = "升序还是降序", allowableValues = "ASC,DESC", example = "ASC")
    private String sortDirection = "ASC";
}
