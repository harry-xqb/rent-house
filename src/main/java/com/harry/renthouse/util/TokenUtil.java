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

    private static final int DEFAULT_TOKEN_EXPIRE_TIME = 60 * 60 * 2; // 2个小时

    private static final String  LOGIN_TOKEN_PREFIX = "LOGIN:TOKEN:";


    /**
     * 生成token
     * @param username 用户名
     * @return token
     */
    public String generate(String username){
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(LOGIN_TOKEN_PREFIX + token, username, DEFAULT_TOKEN_EXPIRE_TIME);
        return token;
    }

    /**
     * 刷新token
     */
    public Boolean refresh(String token){
        return redisUtil.expire(LOGIN_TOKEN_PREFIX + token, DEFAULT_TOKEN_EXPIRE_TIME);
    }

    /**
     * 判断token是否存在
     */
    public Boolean hasToken(String token){
        return redisUtil.hasKey(LOGIN_TOKEN_PREFIX + token);
    }

    /**
     * 通过token获取用户名
     */
    public String getUsername(String token){
        return (String) redisUtil.get(LOGIN_TOKEN_PREFIX + token);
    }
}
