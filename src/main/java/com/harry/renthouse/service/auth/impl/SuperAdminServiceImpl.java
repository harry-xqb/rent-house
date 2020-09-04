package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.entity.HouseStar;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.repository.HouseStarRepository;
import com.harry.renthouse.service.auth.SuperAdminService;
import com.harry.renthouse.service.cache.RedisStarService;
import com.harry.renthouse.service.house.HouseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 超级管理员服务实现
 * @author Harry Xu
 * @date 2020/9/4 10:49
 */
@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    @Resource
    private RedisStarService redisStarService;
    

    @Override
    public void syncStarToRedisFromDatabase() {
        redisStarService.syncStarFromDatabase();
    }

    @Override
    public void syncStarToDatabaseFromRedis() {
        redisStarService.syncStarToDatabase();
    }
}
