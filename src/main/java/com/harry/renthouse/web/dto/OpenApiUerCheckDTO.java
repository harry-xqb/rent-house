package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Harry Xu
 * @date 2020/7/6 18:12
 */
@Data
@ApiModel("开放API检查用户是否对象")
@AllArgsConstructor
public class OpenApiUerCheckDTO {

    @ApiModelProperty(value = "是否存在")
    private boolean exist;

    @ApiModelProperty(value = "描述信息")
    public String message;

}
