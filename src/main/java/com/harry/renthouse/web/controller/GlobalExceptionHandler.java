package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *  全局异常处理
 * @author Harry Xu
 * @date 2020/5/9 15:20
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 参数校验异常数据
     * @param e 参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse paramViolationHandler(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        StringBuilder sb = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach(item -> sb.append(item.getDefaultMessage()).append(";"));
        log.error("参数错误:{}", sb);
        return ApiResponse.ofMessage(ApiResponseEnum.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ApiResponse businessExceptionHandler(BusinessException businessException){
        return ApiResponse.ofMessage(businessException.getCode(), businessException.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResponse exceptionHandler(Exception e){
        e.printStackTrace();
        return ApiResponse.ofMessage(ApiResponseEnum.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
