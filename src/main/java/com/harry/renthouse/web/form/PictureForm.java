package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *  房屋照片对象
 * @author Harry Xu
 * @date 2020/5/11 15:47
 */
@Data
@ApiModel
public class PictureForm {

    @ApiModelProperty(value = "id", example = "1")
    private Long id;

    @ApiModelProperty(value = "宽度", example = "50")
    private Integer width;

    @ApiModelProperty(value = "高度", example = "50")
    private Integer height;

    @NotNull(message = "照片路径不能为空")
    @ApiModelProperty(value = "图片path/上传后的hash值")
    private String path;
}
