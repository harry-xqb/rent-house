package com.harry.renthouse.web.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/6/28 13:45
 */
@Data
public class DistanceSearchForm {

    @NotNull(message = "经度不能为空")
    private double lon;

    @NotNull(message = "纬度不能为空")
    private double lat;

    @NotNull(message = "距离范围不能为空")
    @Min(value = 1, message = "距离范围最少为1千米")
    private Integer distance;
}
