package com.harry.renthouse.util;

import java.util.UUID;

/**
 * token工具类
 * @author Harry Xu
 * @date 2020/5/11 11:45
 */
public class TokenUtil {

    public static final int DEFAULT_TOKEN_EXPIRE_TIME = 60 * 60 * 2; // 2个小时

    public static String generate(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
