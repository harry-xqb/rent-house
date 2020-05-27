package com.harry.renthouse.util;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * 文件上传检查器
 * @author Harry Xu
 * @date 2020/5/27 11:44
 */

public class FileUploaderChecker {

    public static void validSize(long limit, long size){
        if(size > limit){
            throw new BusinessException(MessageFormat
                    .format(ApiResponseEnum.FILE_SIZE_EXCEED_ERROR.getMessage(), limit),
                    ApiResponseEnum.FILE_SIZE_EXCEED_ERROR.getCode());
        }
    }

    public static void validType(String[] limits, String fileName){
        Arrays.stream(limits).filter(item -> StringUtils.endsWith(fileName, item)).findFirst()
                .orElseThrow(() -> new BusinessException(ApiResponseEnum.FILE_TYPE_UNSUPPORTED_ERROR));
    }

    public static void validTypeAndSize(String[] limitType, String fileName, long limitSize, long size){
        validSize(limitSize, size);
        validType(limitType, fileName);
    }
}
