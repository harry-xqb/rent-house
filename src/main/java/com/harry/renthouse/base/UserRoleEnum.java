package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  用户角色枚举
 * @author Harry Xu
 * @date 2020/5/22 15:46
 */
@Getter
@AllArgsConstructor
public enum  UserRoleEnum {
    ADMIN("ADMIN", "管理员"),
    SUPER_ADMIN("SUPER-ADMIN", "超级管理员"),
    USER("USER", "用户"),;

    private String value;

    private String message;
}
