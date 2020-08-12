package com.harry.renthouse.validate.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 验证码类型枚举
 * @author Harry Xu
 * @date 2020/5/22 11:42
 */
@Getter
@AllArgsConstructor
public enum ValidateCodeTypeEnum {
    SIGN_UP("signUp", "注册"),
    LOGIN("login", "登录"),
    RESET_PASSWORD("resetPassword", "重置密码"),
    ;
    private String value;

    private String message;

    public static Optional<ValidateCodeTypeEnum> fromValue(String value){
        return Arrays.stream(ValidateCodeTypeEnum.values()).filter(item -> StringUtils.equals(item.getValue(), value))
                .findFirst();
    }
}
