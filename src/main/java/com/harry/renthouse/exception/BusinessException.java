package com.harry.renthouse.exception;

import com.harry.renthouse.base.ApiResponseEnum;
import lombok.Data;

import java.util.function.Supplier;

/**
 * 自定义业务异常
 * @author Harry Xu
 * @date 2020/5/9 14:41
 */
@Data
public class BusinessException extends RuntimeException {

    private Integer code;

    private String message;

    public BusinessException(ApiResponseEnum apiResponseEnum) {
        super(apiResponseEnum.getMessage());
        this.code = apiResponseEnum.getCode();
        this.message = apiResponseEnum.getMessage();
    }

    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
