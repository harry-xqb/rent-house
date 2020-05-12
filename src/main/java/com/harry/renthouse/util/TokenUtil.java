package com.harry.renthouse.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * token工具类
 * @author Harry Xu
 * @date 2020/5/11 11:45
 */
@Component
public class TokenUtil {

    @Autowired
    private RedisUtil redisUtil;

    public static final int DEFAULT_TOKEN_EXPIRE_TIME = 60 * 60 * 2; // 2个小时

    /**
     * 生成token
     * @param username 用户名
     * @return token
     */
    public String generate(String username){
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(token, username, DEFAULT_TOKEN_EXPIRE_TIME);
        return token;
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public Boolean refresh(String token){
        boolean isSuccess = redisUtil.expire(token, DEFAULT_TOKEN_EXPIRE_TIME);
        return isSuccess;
    }

    /**
     * 判断token是否存在
     * @param token
     */
    public Boolean hasToken(String token){
        return redisUtil.hasKey(token);
    }

    /**
     * 通过token获取用户名
     * @param token
     */
    public String getUsername(String token){
        return (String) redisUtil.get(token);
    }
}
