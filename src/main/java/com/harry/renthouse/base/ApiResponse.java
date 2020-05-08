package com.harry.renthouse.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一 api数据响应
 * @author Harry Xu
 * @date 2020/5/8 10:30
 */
@Data
@AllArgsConstructor
public class ApiResponse {

    private Integer code;

    private String message;

    private Object data;

    public static ApiResponse ofMessage(int code, String message){
        return new ApiResponse(code, message, null);
    }

    public static ApiResponse ofSuccess(Object data){
        return new ApiResponse(ApiResponseEnum.SUCCESS.getCode(), ApiResponseEnum.SUCCESS.getMessage(), data);
    }
    public static ApiResponse ofSuccess(){
        return ApiResponse.ofSuccess(null);
    }
    public static ApiResponse ofStatus(ApiResponseEnum apiResponseEnum){
        return ApiResponse.ofMessage(apiResponseEnum.getCode(), apiResponseEnum.getMessage());
    }
}
