package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/6/1 18:04
 */
@Data
public class MapBoundSearchForm {

    @ApiModelProperty(value = "左上角经度", required = true)
    @NotNull(message = "左上角经度不能为空")
    private Double leftTopLongitude;

    @ApiModelProperty(value = "左上角纬度", required = true)
    @NotNull(message = "左上角纬度不能为空")
    private Double leftTopLatitude;

    @ApiModelProperty(value = "右下角经度", required = true)
    @NotNull(message = "右下角经度不能为空")
    private Double rightBottomLongitude;

    @ApiModelProperty(value = "右下角纬度", required = true)
    @NotNull(message = "右下角纬度不能为空")
    private Double rightBottomLatitude;
}
