package com.harry.renthouse.service.cache.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.HouseStar;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.repository.HouseStarRepository;
import com.harry.renthouse.service.cache.RedisStarService;
import com.harry.renthouse.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜藏（点赞）缓存
 * @author Harry Xu
 * @date 2020/9/2 15:34
 */
@Service
@Slf4j
public class RedisStarServiceImpl implements RedisStarService {

    private static final String HOUSE_STAR_ZSET_PREFIX = "star:house:";

    private static final String USER_STAR_ZSET_PREFIX = "star:user:";

    private static final String STAR_SYNC_LOCK = "star:sync:lock";

    // 取消收藏数据库中数据操作记录
    private static final String STAR_RECORD_ZSET_PREFIX = "star:record:add";

    private static final String STAR_RECORD_DELETE_KEY = "star:record:delete";
    // 数据库star数据hash表
    private static final String STAR_RECORD_DATABASE_KEY= "star:record:database";

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private HouseStarRepository houseStarRepository;

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
        redisUtil.zSetAdd(houseKey, userId, score);
        // 用户搜藏列表添加房屋id
        redisUtil.zSetAdd(userKey, houseId, score);
        // 收藏记录zset
        String value = userId + "-" + houseId;
        redisUtil.zSetAdd(STAR_RECORD_ZSET_PREFIX, value, score);
    }

    @Override
    public Page<HouseStar> findAllByUserId(Long userId, Pageable pageable, Sort.Direction direction) {
        String userKey = USER_STAR_ZSET_PREFIX + userId;
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = pageNumber * pageSize;
        int end = Math.max((start + pageSize - 1), 0);
        Set<ZSetOperations.TypedTuple<Object>> userStarSet = null;
        // 创建时间升序
        if(direction.isAscending()){
            userStarSet = redisUtil.zSetRangeWithScores(userKey, start, end);
        }else{
            // 降序
            userStarSet = redisUtil.zSetReverseRangeWithScores(userKey, start, end);
        }
        List<HouseStar> list = new ArrayList<>();
        for(ZSetOperations.TypedTuple<Object> userStar: userStarSet){
            String houseId = String.valueOf(userStar.getValue());
            long createTime =  userStar.getScore().longValue();
            Date date = new Date(createTime);
            HouseStar houseStar = new HouseStar();
            houseStar.setHouseId(Long.valueOf(houseId));
            houseStar.setCreateTime(date);
            houseStar.setLastUpdateTime(date);
            houseStar.setUserId(userId);
            list.add(houseStar);
        }
        int total = (int) redisUtil.zSetGetSize(userKey);
        return new PageImpl<>(list, pageable, total);
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
        // 取消收藏列表添加数据(如果当前数据中存在于数据库，则添加到需要删除的集合中)
        String hashKey = userId + "-" + houseId;
        Integer starIdObj = (Integer) redisUtil.hget(STAR_RECORD_DATABASE_KEY, hashKey);
        if(starIdObj != null){


            long starId = Long.valueOf(starIdObj);
            redisUtil.sSet(STAR_RECORD_DELETE_KEY,  starId);
        }
    }

    @Override
    public void syncStarFromDatabase() {
        List<HouseStar> houseList = houseStarRepository.findAll();
        redisUtil.pipeLine(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // 批量同步mysql中收藏数据到redis
                Map map = new HashMap();
                for(HouseStar houseStar: houseList){
                    Long userId = houseStar.getUserId();
                    Long houseId = houseStar.getHouseId();
                    long score = houseStar.getCreateTime().getTime();
                    String houseKey = HOUSE_STAR_ZSET_PREFIX + houseId;
                    String userKey = USER_STAR_ZSET_PREFIX + userId;
                    operations.opsForZSet().add(houseKey, userId, score);
                    operations.opsForZSet().add(userKey, houseId, score);
                    // 更新hash数据到redis
                    map.put(userId + "-" + houseId, houseStar.getId());
                }
                operations.opsForHash().putAll(STAR_RECORD_DATABASE_KEY, map);
                return null;
            }
        });
    }

    @Transactional
    public void syncStarToDatabase(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 取消
        if(redisUtil.setNotExist(STAR_SYNC_LOCK, uuid, 30)){
            log.info("正在获取新增的收藏操作记录表...");
            // 获取收藏列表
            Set<ZSetOperations.TypedTuple<Object>> starRecordSet = redisUtil.zSetRangeWithScores(STAR_RECORD_ZSET_PREFIX, 0, -1);
            // 获取取消收藏列表
            log.info("正造获取需要删除的收藏操作记录...");
            Set<Object> deletedStarSet = redisUtil.sGet(STAR_RECORD_DELETE_KEY);
            List<Long> deleteStarIdList = new ArrayList<>();
            if(deletedStarSet != null){
                deleteStarIdList = deletedStarSet.stream().map(item -> Long.parseLong(item.toString())).collect(Collectors.toList());
            }
            List<HouseStar> list = new ArrayList<>();
            // 遍历出star record
            for(ZSetOperations.TypedTuple<Object> starRecord: starRecordSet){
                String recordValue = (String) starRecord.getValue();
                Double createTime = starRecord.getScore();
                if(StringUtils.isNotBlank(recordValue) && createTime != null){
                    String[] split = recordValue.split("-");
                    if(split.length == 2){
                        long userId = Long.parseLong(split[0]);
                        long houseId = Long.parseLong(split[1]);
                        HouseStar houseStar = new HouseStar();
                        houseStar.setUserId(userId);
                        houseStar.setHouseId(houseId);
                        Date date = new Date(createTime.longValue());
                        houseStar.setCreateTime(date);
                        houseStar.setLastUpdateTime(date);
                        list.add(houseStar);
                    }
                }
            }
            // 移除需要删除的房源id
            log.info("正在删除取消收藏的数据...");
            houseStarRepository.deleteAllByIdIn(deleteStarIdList);
            houseStarRepository.flush();
            // 新增的star存入mysql中
            log.info("正在保存新增的收藏数据...");
            List<HouseStar> saveResult = houseStarRepository.saveAll(list);
            // 更新starHash, record记录表, 删除记录表
            log.info("正造删除redis记录表中数据...");
            redisUtil.pipeLine(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    Map<String, Long> map = new HashMap<>();
                    for (HouseStar houseStar : saveResult) {
                        Long houseId = houseStar.getHouseId();
                        Long userId = houseStar.getUserId();
                        Long starId = houseStar.getId();
                        map.put(userId + "-" + houseId, starId);
                    }
                    // 更新starHash
                    operations.opsForHash().putAll(STAR_RECORD_DATABASE_KEY, map);
                    // 清空record记录表
                    operations.delete(STAR_RECORD_ZSET_PREFIX);
                    operations.delete(STAR_RECORD_DELETE_KEY);
                    return null;
                }
            });
            // 释放锁
            log.info("正造释放同步锁...");
            redisUtil.del(STAR_SYNC_LOCK);
            log.info("同步任务结束。本次共同步新增收藏数据:{}条，删除收藏数据:{}条", list.size(), deleteStarIdList.size());
        }else{
            log.warn("当前数据正在同步中，为避免数据紊乱， 不可重复同步。");
        }
    }

}
