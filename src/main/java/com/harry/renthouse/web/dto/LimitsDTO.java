package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Harry Xu
 * @date 2020/5/27 11:36
 */
@Data
@ApiModel("通用请求接口限制 DTO")
public class LimitsDTO {

    @ApiModelProperty(value = "用户密码正则")
    private String userPasswordRegex;

    @ApiModelProperty(value = "手机号正则")
    public String phoneRegex;

    @ApiModelProperty(value = "头像上传大小: 5m")
    public long avatarSizeLimit;

    @ApiModelProperty(value = "头像类型限制")
    public String[] avatarTypeLimit;
}
