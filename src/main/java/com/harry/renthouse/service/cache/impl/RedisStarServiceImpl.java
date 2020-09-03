package com.harry.renthouse.service.cache.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.service.cache.RedisStarService;
import com.harry.renthouse.util.RedisUtil;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 搜藏（点赞）缓存
 * @author Harry Xu
 * @date 2020/9/2 15:34
 */
@Service
public class RedisStarServiceImpl implements RedisStarService {

    private static final String HOUSE_STAR_ZSET_PREFIX = "star:house:";

    private static final String USER_STAR_ZSET_PREFIX = "star:user:";

    private static final String STAR_RECORD_ZSET_PREFIX = "star:record";

    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean isStar(Long userId, Long houseId) {
        String key = USER_STAR_ZSET_PREFIX + userId;
        return redisUtil.zSetIsMember(key, houseId);
    }

    @Override
    public long getHouseStarCount(Long houseId) {
        String key = HOUSE_STAR_ZSET_PREFIX + houseId;
        return redisUtil.zSetGetSize(key);
    }

    @Override
    public void star(Long userId, Long houseId) {
        if(isStar(userId, houseId)){
            throw new BusinessException(ApiResponseEnum.HOUSE_STAR_REPEAT_ERROR);
        }
        String houseKey = HOUSE_STAR_ZSET_PREFIX + houseId;
        String userKey = USER_STAR_ZSET_PREFIX + userId;
        long score = new Date().getTime();
        // 房屋被搜藏列表添加用户id
        redisUtil.zSet(houseKey, userId, score);
        // 用户搜藏列表添加房屋id
        redisUtil.zSet(userKey, houseId, score);
        // 收藏记录zset
        redisUtil.zSet(STAR_RECORD_ZSET_PREFIX, userId + "-" + houseId, score);
    }

    @Override
    public void unStar(Long userId, Long houseId) {
        if(!isStar(userId, houseId)){
            throw new BusinessException(ApiResponseEnum.HOUSE_UN_STAR_NOT_FOUND_ERROR);
        }
        String houseKey = HOUSE_STAR_ZSET_PREFIX + houseId;
        String userKey = USER_STAR_ZSET_PREFIX + userId;
        long time = new Date().getTime();
        // 房屋被搜藏列表添加用户id
        redisUtil.zSetRemove(houseKey, userId);
        // 用户搜藏列表添加房屋id
        redisUtil.zSetRemove(userKey, houseId);
        // 收藏记录zset
        redisUtil.zSetRemove(STAR_RECORD_ZSET_PREFIX, userId + "-" + houseId);
    }

}
