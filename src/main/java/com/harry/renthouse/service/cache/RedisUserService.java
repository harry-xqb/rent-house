package com.harry.renthouse.service.cache;

import com.harry.renthouse.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Harry Xu
 * @date 2020/9/1 15:46
 */
public interface RedisUserService {

    /**
     * 缓存用户信息(key分别为手机号, id, 用户名)
     * @param user 用户
     */
    void addUser(User user);

    /**
     * 通过用户id获取用户信息
     * @param userId 用户id
     */
    Optional<User> getUserById(Long userId);

    /**
     * 通过用户名称获取用户信息
     * @param name 用户名称
     */
    Optional<User> getUserByName(String name);

    /**
     * 通过手机号获取用户信息
     * @param phoneNumber 手机号
     */
    Optional<User> getUserByPhoneNumber(String phoneNumber);

    /**
     * 添加用户角色
     * @param userId 用户id
     * @param roles 角色
     */
    void addUserRoles(Long userId, String[] roles);

    /**
     * 获取用户的角色
     * @param userId 用户id
     */
    Set<String> getUserRoles(Long userId);

    /**
     * 添加重置密码的token, key: token  value: 手机号,
     * @param phoneNumber 手机号
     * @return 生成的token
     */
    String addResetPasswordToken(String phoneNumber);

    /**
     * 通过token获取重置密码的手机号, 获取一次后token将被删除
     * @param token 重置密码token
     * @return 手机号
     */
    String getPhoneByResetPasswordToken(String token);
}
