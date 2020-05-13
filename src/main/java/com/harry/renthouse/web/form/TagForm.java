package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/5/12 17:19
 */
@Data
@ApiModel
public class TagForm {

    @NotNull(message = "标签名称不能为空")
    @ApiModelProperty(value = "标签名称", required = true, example = "精装修")
    private String name;

    @NotNull(message = "房屋id不能为空")
    @ApiModelProperty(value = "房屋id", required = true)
    private Long houseId;
}
