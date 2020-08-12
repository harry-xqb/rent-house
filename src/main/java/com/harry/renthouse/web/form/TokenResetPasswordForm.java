package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PasswordStrongAnnotation;
import com.harry.renthouse.web.form.annotation.PhoneAnnotation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/8/11 17:48
 */
@Data
public class TokenResetPasswordForm {

    @NotNull(message = "令牌不能为空")
    private String token;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    @PasswordStrongAnnotation()
    private String password;
}
