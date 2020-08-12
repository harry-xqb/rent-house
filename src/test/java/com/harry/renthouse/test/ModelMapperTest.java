package com.harry.renthouse.test;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.web.form.UserBasicInfoForm;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import javax.annotation.Resource;

/**
 * @author Harry Xu
 * @date 2020/8/9 17:43
 */
public class ModelMapperTest extends RentHouseApplicationTests {

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private UserRepository userRepository;

    @Test
    void testMapper(){
        User user = userRepository.findById(2L).orElse(null);
        System.out.println(user);
        UserBasicInfoForm userForm = new UserBasicInfoForm();
        userForm.setAvatar("abcd");
        userForm.setIntroduction("如此简单");
        modelMapper.map(userForm, user);
        System.out.println(user);
    }
}
