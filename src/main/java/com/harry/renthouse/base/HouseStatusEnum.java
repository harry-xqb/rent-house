package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Harry Xu
 * @date 2020/5/12 10:50
 */
@Getter
@AllArgsConstructor
public enum HouseStatusEnum {
    NOT_AUDITED(0, "未审核"),
    AUDIT_PASSED(1, "审核通过"),
    RENTED(2, "已出租"),
    DELETED(3, "已删除"), // 逻辑删除
    ;
    private int value;

    private String msg;
}
