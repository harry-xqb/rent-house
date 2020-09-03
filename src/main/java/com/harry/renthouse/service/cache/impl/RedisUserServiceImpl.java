package com.harry.renthouse.service.cache.impl;

import com.harry.renthouse.config.RedisConfig;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.service.cache.RedisUserService;
import com.harry.renthouse.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户信息缓存
 * @author Harry Xu
 * @date 2020/9/1 15:48
 */
@Service
public class RedisUserServiceImpl implements RedisUserService {

    private static final String REDIS_USER_ID_PREFIX = "user:id:";
    private static final String REDIS_USER_NAME_PREFIX = "user:name:";
    private static final String REDIS_USER_PHONE_PREFIX = "user:phone:";
    private static final String REDIS_USRE_ROLE_PREFIX = "role:user:";

    // 重置密码令牌前缀
    private static final String RESET_PASS_WORD_TOKEN_PREFIX = "RESET:PASSWORD:TOKEN:";

    // 15分钟有效
    private static final int RESET_PASS_WORD_TOKEN_EXPIRE = 60 * 15;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void addUser(User user) {
        String idKey = REDIS_USER_ID_PREFIX + user.getId();
        String nameKey = REDIS_USER_NAME_PREFIX + user.getName();
        String phoneKey = REDIS_USER_PHONE_PREFIX + user.getPhoneNumber();
        redisUtil.set(idKey, user, RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE);
        redisUtil.set(nameKey, user, RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE);
        redisUtil.set(phoneKey, user, RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        User user = (User) redisUtil.get(REDIS_USER_ID_PREFIX + userId);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByName(String name) {
        User user = (User) redisUtil.get(REDIS_USER_NAME_PREFIX + name);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        User user = (User) redisUtil.get(REDIS_USER_PHONE_PREFIX + phoneNumber);
        return Optional.ofNullable(user);
    }

    @Override
    public void addUserRoles(Long userId, String[] roles) {
        redisUtil.sSetAndTime(REDIS_USRE_ROLE_PREFIX + userId, RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE, roles);
    }

    @Override
    public Set<String> getUserRoles(Long userId) {
        Set<Object> roles = redisUtil.sGet(REDIS_USRE_ROLE_PREFIX + userId);
        if(!CollectionUtils.isEmpty(roles)){
            return roles.stream().map(item -> (String) item).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    @Override
    public String addResetPasswordToken(String phoneNumber) {
        String token = UUID.randomUUID().toString();
        redisUtil.set(RESET_PASS_WORD_TOKEN_PREFIX + token, phoneNumber, RESET_PASS_WORD_TOKEN_EXPIRE);
        return token;
    }

    @Override
    public String getPhoneByResetPasswordToken(String token) {
        String phoneNumber = (String) redisUtil.get(RESET_PASS_WORD_TOKEN_PREFIX + token);
        redisUtil.del(RESET_PASS_WORD_TOKEN_PREFIX + token);
        return phoneNumber;
    }
}
