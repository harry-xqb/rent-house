package com.harry.renthouse.redis_sentinel;

import com.harry.renthouse.RentHouseApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/9/3 13:47
 */
public class RedisPipeLineTest extends RentHouseApplicationTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void testPipeLineSet(){
        long start = new Date().getTime();
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for(int i = 0; i < 50000; i++){
                     operations.opsForValue().set("hello" + i, i);
                     if(i % 1000 == 0){
                         System.out.println("插入数据：" + i);
                     }
                }
                return null;
            }
        });
        long end = new Date().getTime();
        // 用时1.5秒
        System.out.println("使用管道插入5万条数据用时:" + (end - start) + "ms");
    }

    @Test
    public void testPipeLineGet(){
        long start = new Date().getTime();
        List list = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (int i = 0; i < 50000; i++) {
                    operations.opsForValue().get("hello" + i);
                    if (i % 1000 == 0) {
                        System.out.println("获取：" + i);
                    }
                }
                return null;
            }
        });
        long end = new Date().getTime();
        // 用时1.6秒
        System.out.println("使用管道获取5万条数据用时:" + (end - start) + "ms");
    }

    @Test
    public void testNoPipeLineSet(){
        long start = new Date().getTime();
        for(int i = 0; i < 50000; i++){
            redisTemplate.opsForValue().set("hello" + i, i);
        }
        long end = new Date().getTime();
        // 用时40秒
        System.out.println("未使用管道插入5万条数据用时:" + (end - start) + "ms");
    }

    @Test
    public void testNoPipeLineGet(){
        long start = new Date().getTime();
        for(int i = 0; i < 50000; i++){
            Object result = redisTemplate.opsForValue().get("hello" + i);
            System.out.println(result);
        }
        long end = new Date().getTime();
        // 用时40秒
        System.out.println("未使用管道获取万条数据用时:" + (end - start) + "ms");
    }
}
