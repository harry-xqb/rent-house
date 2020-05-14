package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 出租方式枚举
 * @author Harry Xu
 * @date 2020/5/14 11:42
 */
@AllArgsConstructor
@Getter
public enum  RentWayEnum {

    SHARE(0, "合租"),
    WHOLE(1, "整租");

    private int value;

    private String message;

    public static Optional<RentWayEnum> fromValue(int value){
        return Arrays.stream(RentWayEnum.values()).filter(item -> item.getValue() == value).findFirst();
    }
}
