package com.harry.renthouse.task;

import com.harry.renthouse.service.cache.RedisStarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 每天凌晨3点定时同步houseStar数据到mysql
 * @author Harry Xu
 * @date 2020/9/5 14:49
 */
@Component
@Slf4j
@EnableScheduling
public class HouseStarSyncTask {

    @Resource
    private RedisStarService redisStarService;

    @Scheduled(cron = "0 0 3 * * *")
    public void syncHouseStarToDatabase(){
       // redisStarService.
        log.info("定时任务:同步redis收藏房源数据到数据库开始");
        redisStarService.syncStarToDatabase();
        log.info("定时任务:同步redis收藏房源数据到数据库结束");
    }
}
