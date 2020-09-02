package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.text.MessageFormat;

/**
 *  全局异常处理
 * @author Harry Xu
 * @date 2020/5/9 15:20
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxUploadSize;

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

    @ExceptionHandler(BusinessException.class)
    public ApiResponse businessExceptionHandler(BusinessException businessException){
        return ApiResponse.ofMessage(businessException.getCode(), businessException.getMessage());
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ApiResponse fileSizeExceptionHandler(MaxUploadSizeExceededException e){
        log.error("文件上传大小超限:{}", e.getMessage());
        return ApiResponse.ofMessage(ApiResponseEnum.FILE_SIZE_EXCEED_ERROR.getCode(),
                MessageFormat.format(ApiResponseEnum.FILE_SIZE_EXCEED_ERROR.getMessage(), maxUploadSize));
    }


    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ApiResponse exceptionHandler(HttpRequestMethodNotSupportedException e){
        log.error("不支持的请求类型异常:{}", e.getMessage());
        return ApiResponse.ofStatus(ApiResponseEnum.UNSUPPORTED_REQUEST_TYPE);
    }

    @ExceptionHandler(value = Exception.class)
    public ApiResponse exceptionHandler(Exception e){
        log.error("不支持的请求类型异常:{}", e.getMessage());
        return ApiResponse.ofStatus(ApiResponseEnum.INTERNAL_SERVER_ERROR);
    }
}
