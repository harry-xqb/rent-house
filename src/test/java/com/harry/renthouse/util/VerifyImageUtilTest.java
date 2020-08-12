package com.harry.renthouse.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/8/10 11:39
 */
class VerifyImageUtilTest {

    @Test
    void create() throws IOException {
        /*Map<String, String> stringStringMap = VerifyImageUtil.create(30, 30);
        System.out.println(stringStringMap);*/

        Random random = new Random();
        for(int i = 0; i < 100; i++){
            System.out.println(random.nextInt(3) + 1);
        }
    }
}