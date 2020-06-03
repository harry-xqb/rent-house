package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PasswordStrongAnnotation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Harry Xu
 * @date 2020/6/3 13:38
 */
@Data
public class NoPassLoginForm {

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^(1[3-9]\\d{9}$)", message = "手机号格式有误")
    private String phoneNumber;

    @NotNull(message = "验证码不能为空")
    @ApiModelProperty(value = "手机验证码", required = true)
    private String verifyCode;
}
