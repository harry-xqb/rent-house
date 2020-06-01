package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    @ApiModelProperty(value = "排序方式", example = "desc", allowableValues = "desc,asc")
    private String orderDirection = "desc";

    @Min(value = 1, message = "页号不能小于1")
    @ApiModelProperty(value = "页号， 默认为1， 不能小于1", example = "1")
    private int page = 1;

    @Min(value = 1, message = "页面大小不能小于1")
    @ApiModelProperty(value = "页面大小， 默认为5， 不能小于1", example = "15")
    private int pageSize = 5;

}
