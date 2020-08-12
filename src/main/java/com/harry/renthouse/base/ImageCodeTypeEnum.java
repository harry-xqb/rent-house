package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/8/10 13:23
 */
@Getter
@AllArgsConstructor
public enum ImageCodeTypeEnum {

    LOGIN(1, "登录"),
    REGISTER(2, "注册"),
    RESET_PASSWORD(3, "重置密码"),
    ;

    private int value;

    private String message;

    public Optional<ImageCodeTypeEnum> ofValue(int value){
        return Arrays.stream(ImageCodeTypeEnum.values()).filter(item -> item.getValue() == value).findFirst();
    }
}
