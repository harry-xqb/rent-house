package com.harry.renthouse.service.auth;

import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.web.dto.UserDTO;
import com.harry.renthouse.web.form.UserBasicInfoForm;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;

import java.util.List;

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
    UserDTO findUserById(Long id);

    /**
     * 通过手机号查询用户
     * @param phoneNumber
     * @return
     */
    UserDTO findByPhoneNumber(String phoneNumber);

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
}
