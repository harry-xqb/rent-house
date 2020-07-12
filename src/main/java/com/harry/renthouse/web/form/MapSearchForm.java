package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/6/1 17:38
 */
@Data
@ApiModel("地图查询表单")
public class MapSearchForm {

    @ApiModelProperty(value = "城市英文名", required = true, example = "bj")
    @NotNull(message = "城市不能为空")
    private String cityEnName;

    @ApiModelProperty(value = "排序字段", example = "lastUpdateTime")
    private String orderBy = "lastUpdateTime";

    @ApiModelProperty(value = "排序方式", example = "DESC", allowableValues = "ASC,DESC")
    private String sortDirection = "DESC";

    @Min(value = 1, message = "页号不能小于1")
    @ApiModelProperty(value = "页号， 默认为1， 不能小于1", example = "1")
    private int page = 1;

    @Min(value = 1, message = "页面大小不能小于1")
    @ApiModelProperty(value = "页面大小， 默认为5， 不能小于1", example = "15")
    private int pageSize = 5;

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

    @ApiModelProperty(value = "视野范围")
    private MapBoundSearchForm bounds;
}
