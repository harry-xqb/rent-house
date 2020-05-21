package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * 支持排序字段
 * @author Harry Xu
 * @date 2020/5/18 16:52
 */
@Getter
@AllArgsConstructor
public enum HouseSortOrderByEnum {
    DEFAULT("lastUpdateTime", "默认-最新"),
    NEWEST("lastUpdateTime", "最近更新时间"),
    PRICE("price", "价格"),
    AREA("area", "房屋面积"),
    DISTANCE_TO_SUBWAY("distanceToSubway", "到地铁距离"),
    ;

    private String value;

    private String message;

    public static Optional<HouseSortOrderByEnum> from(String key){
        return Arrays.stream(HouseSortOrderByEnum.values()).filter(item -> StringUtils.equals(key, item.getValue())).findFirst();
    }
}
