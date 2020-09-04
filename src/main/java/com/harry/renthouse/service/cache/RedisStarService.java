package com.harry.renthouse.service.cache;

import com.harry.renthouse.entity.HouseStar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

/**
 * @author Harry Xu
 * @date 2020/9/2 15:33
 */
public interface RedisStarService {

    /**
     * 返回用户是否搜藏过房子
     * @param userId 用户id
     * @param houseId 房屋id
     */
    boolean isStar(Long userId, Long houseId);

    /**
     * 获取房屋收藏数量
     * @param houseId 帖子 id
     * @return 房屋被收藏的数量
     */
    long getHouseStarCount(Long houseId);


    /**
     * 点赞
     * @param userId 用户 id
     * @param houseId 房屋 id
     */
    void star(Long userId, Long houseId);

    /**
     * 分页查找用户收藏数据
     * @param userId 用户id
     * @param pageable 分页
     */
    Page<HouseStar> findAllByUserId(Long userId, Pageable pageable, Sort.Direction direction);

    /**
     * 取消点赞
     * @param userId 用户 id
     * @param houseId 房屋 id
     */
    void unStar(Long userId, Long houseId);

    /**
     * 从数据库中同步收藏信息到redis中（一般只在redis初始化时第一次使用）
     */
    void syncStarFromDatabase();

    /**
     * 手动同步redis中的数据到Database,
     * （redis中的收藏数据会定时同步到mysql中，如非必要，无需手动同步）
     */
    void syncStarToDatabase();
}
