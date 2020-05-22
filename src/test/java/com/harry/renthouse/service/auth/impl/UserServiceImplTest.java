package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/22 15:44
 */
class UserServiceImplTest extends RentHouseApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void registerUserByPhone() {
        UserPhoneRegisterForm userPhoneRegisterForm = new UserPhoneRegisterForm();
        userPhoneRegisterForm.setPassword("12345678q");
        userPhoneRegisterForm.setPhoneNumber("17879502601");
        userService.registerUserByPhone(userPhoneRegisterForm, Collections.singletonList(UserRoleEnum.ADMIN));
    }
}