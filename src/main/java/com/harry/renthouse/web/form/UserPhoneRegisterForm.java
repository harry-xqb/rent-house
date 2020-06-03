package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PasswordStrongAnnotation;
import com.harry.renthouse.web.form.annotation.PhoneAnnotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 *  用户手机号注册表单
 * @author Harry Xu
 * @date 2020/5/22 10:10
 */
@Data
@ApiModel("用户手机号注册表单")
public class UserPhoneRegisterForm {

    @ApiModelProperty(value = "手机号", required = true)
    @NotNull(message = "手机号不能为空")
    @PhoneAnnotation()
    private String phoneNumber;

    @ApiModelProperty(value = "登录密码", required = true)
    @NotNull(message = "密码不能为空")
    @PasswordStrongAnnotation()
    private String password;

    @NotNull(message = "验证码不能为空")
    @ApiModelProperty(value = "手机验证码", required = true)
    private String verifyCode;

}
