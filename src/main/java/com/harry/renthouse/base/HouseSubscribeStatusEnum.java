package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 房屋预约状态枚举
 * @author Harry Xu
 * @date 2020/6/4 17:29
 */
@AllArgsConstructor
@Getter
public enum HouseSubscribeStatusEnum {

    WAIT(1, "待看"),
    ORDERED(2, "已预约"),
    FINISH(3, "已完成");
        ;

    private int value;

    private String message;

    public static HouseSubscribeStatusEnum of(int code){
        return Arrays.stream(HouseSubscribeStatusEnum.values())
                .filter(item -> item.getValue() == code).findFirst().orElse(HouseSubscribeStatusEnum.WAIT);
    }
}
