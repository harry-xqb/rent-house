package com.harry.renthouse.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务端统一单个对象返回格式
 * @author Harry Xu
 * @date 2020/5/9 15:06
 */
@AllArgsConstructor
@Data
public class ServiceResult<T> {

    private Boolean success;

    private String message;

    private T result;

    public ServiceResult(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
