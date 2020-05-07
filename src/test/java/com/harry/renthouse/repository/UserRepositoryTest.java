package com.harry.renthouse.repository;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Harry Xu
 * @date 2020/5/7 16:17
 */

public class UserRepositoryTest extends RentHouseApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void  findOneTest(){
        User user = userRepository.findById(2L).orElse(null);
        Assert.notNull(user, "用户为空");
        Assert.isTrue("admin".equals(user.getName()), "id为2的用户名称不为admin");
    }
}