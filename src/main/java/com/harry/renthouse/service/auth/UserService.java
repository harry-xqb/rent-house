package com.harry.renthouse.service.auth;

import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.web.dto.UserDTO;
import com.harry.renthouse.web.form.UserBasicInfoForm;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;

import java.util.List;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/5/18 18:39
 */
public interface UserService {

    /**
     * 通过用户id查找用户
     * @param id 用户id
     * @return
     */
    Optional<UserDTO> findUserById(Long id);

    /**
     * 通过手机号查询用户
     * @param phoneNumber
     * @return
     */
    Optional<UserDTO> findByPhoneNumber(String phoneNumber);

    /**
     * 更新用户头像
     * @param avatar 头像地址
     */
    void updateAvatar(String avatar);

    /**
     * 更新用户信息
     * @param userBasicInfoForm 用户基本信息
     */
    UserDTO updateUserInfo(Long userId, UserBasicInfoForm userBasicInfoForm);

    /**
     * 通过手机号注册用户
     * @param phoneRegisterForm 手机号注册表单
     * @param roleList 用户角色集合
     */
    UserDTO registerUserByPhone(UserPhoneRegisterForm phoneRegisterForm, List<UserRoleEnum> roleList);

    /**
     * 通过昵称查找用户
     * @param nickName 用户昵称
     */
    Optional<UserDTO> findByNickName(String nickName);

    /**
     * 通过手机号创建用户
     * @param phone 手机号
     */
    UserDTO createByPhone(String phone);


    /**
     * 更新用户密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(String oldPassword, String newPassword);

}
