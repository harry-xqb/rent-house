package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:38
 */
@Data
@ApiModel("手机号密码登录表单")
public class PhonePasswordLoginForm {

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^(1[3-9]\\d{9}$)", message = "手机号格式有误")
    private String phone;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}
