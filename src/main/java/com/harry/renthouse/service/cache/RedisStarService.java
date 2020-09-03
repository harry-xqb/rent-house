package com.harry.renthouse.service.cache;

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
     * 取消点赞
     * @param userId 用户 id
     * @param houseId 房屋 id
     */
    void unStar(Long userId, Long houseId);


}
