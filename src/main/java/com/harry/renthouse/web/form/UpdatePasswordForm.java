package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PasswordStrongAnnotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/6/3 15:17
 */
@Data
@ApiModel("修改密码表单")
public class UpdatePasswordForm {

    @ApiModelProperty(value = "原密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotNull(message = "新密码不能为空")
    @PasswordStrongAnnotation()
    private String newPassword;
}
