package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/7/27 11:33
 */
@Getter
@AllArgsConstructor
public enum  AggTypeEnum {

    REGION("region", "区县"),
    DISTRICT("region", "小区"),
        ;

    private String value;

    private String message;

    public static Optional<AggTypeEnum> fromValue(String value){
        return Arrays.stream(AggTypeEnum.values()).filter(item -> StringUtils.equals(item.getValue(), value)).findFirst();
    }
}
