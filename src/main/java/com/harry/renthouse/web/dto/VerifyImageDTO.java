package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harry Xu
 * @date 2020/8/10 13:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyImageDTO {

    @ApiModelProperty(value = "背景图片")
    private String backImage;

    @ApiModelProperty(value = "滑动图片")
    private String slideImage;

    @ApiModelProperty(value = "y轴坐标")
    private int y;
}
