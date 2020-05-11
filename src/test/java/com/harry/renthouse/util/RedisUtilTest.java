package com.harry.renthouse.util;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:22
 */
class RedisUtilTest extends RentHouseApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    void set() {
        User user = new User();
        user.setName("harry");
        user.setEmail("923243595@qq.com");
        user.setPhoneNumber("17879502601");
        boolean result = redisUtil.set("hello", user);
        Assert.isTrue(result, "redis插入数据失败");
    }

    @Test
    void hasKey() {
        boolean hasKey = redisUtil.hasKey("52cbc2725f1243da8132e741984f0289");
        Assert.isTrue(hasKey, "redis key不存在");
    }
}