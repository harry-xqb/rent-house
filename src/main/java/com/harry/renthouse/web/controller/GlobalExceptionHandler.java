package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 *  全局异常处理
 * @author Harry Xu
 * @date 2020/5/9 15:20
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常数据
     * @param cve 参数校验异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse paramViolationHandler(ConstraintViolationException cve){
        Set<ConstraintViolation<?>> cves = cve.getConstraintViolations();
        StringBuffer errorMsg = new StringBuffer();
        cves.forEach(ex -> errorMsg.append(ex.getMessage()));
        return ApiResponse.ofMessage(ApiResponseEnum.BAD_REQUEST.getCode(), errorMsg.toString());
    }

    @ExceptionHandler(value = BusinessException.class)
    public ApiResponse businessExceptionHandler(BusinessException businessException){
        return ApiResponse.ofMessage(businessException.getCode(), businessException.getMessage());
    }
}
