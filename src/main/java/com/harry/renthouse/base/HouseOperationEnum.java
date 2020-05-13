package com.harry.renthouse.base;

import com.harry.renthouse.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/5/13 9:42
 */
@AllArgsConstructor
@Getter
public enum  HouseOperationEnum {
    PULL_OUT(0, "下架/重新审核"),
    PASS(1, "审核通过"),
    RENT(2, "出租"),
    DELETE(3, "删除");

    private int code;

    private String message;

    public static HouseOperationEnum of(Integer code){
        Optional<HouseOperationEnum> operateOptional = new ArrayList<HouseOperationEnum>(Arrays.asList(HouseOperationEnum.values()))
                .stream().filter(item -> item.code == code).findFirst();
        return operateOptional.orElseThrow(() -> new BusinessException(ApiResponseEnum.UNSUPPORTED_OPERATION));
    }
}
