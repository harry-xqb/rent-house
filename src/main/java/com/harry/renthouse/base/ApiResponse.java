package com.harry.renthouse.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一 api数据响应
 * @author Harry Xu
 * @date 2020/5/8 10:30
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    @ApiModelProperty(value = "状态码: 200为成功, 其他的为失败", example = "200")
    private Integer code;

    @ApiModelProperty(value = "返回信息", example = "成功")
    private String message;

    @ApiModelProperty(value = "返回数据对象")
    private T data;

    public static<T> ApiResponse<T> ofMessage(int code, String message){
        return new ApiResponse<T>(code, message, null);
    }

    public static<T> ApiResponse<T> ofSuccess(T data){
        return new ApiResponse<T>(ApiResponseEnum.SUCCESS.getCode(), ApiResponseEnum.SUCCESS.getMessage(), data);
    }
    public static ApiResponse ofSuccess(){
        return ApiResponse.ofSuccess(null);
    }
    public static ApiResponse ofStatus(ApiResponseEnum apiResponseEnum){
        return ApiResponse.ofMessage(apiResponseEnum.getCode(), apiResponseEnum.getMessage());
    }
}
