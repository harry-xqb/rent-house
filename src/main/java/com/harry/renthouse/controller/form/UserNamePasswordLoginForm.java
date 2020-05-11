package com.harry.renthouse.controller.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:38
 */
@Data
public class UserNamePasswordLoginForm {

    @NotNull(message = "用户名不能为空")
    private String username;

    @NotNull(message = "密码不能为空")
    private String password;
}
