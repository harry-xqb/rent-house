package com.harry.renthouse.service.cache;

import com.harry.renthouse.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/9/1 15:39
 */
public interface RedisService {

    /**
     * redis存用户浏览记录,根据浏览时间, 只存8条
     * @param userId 用户id
     * @param houseId 房源id
     */
    void addBrowseHistory(Long userId, Long houseId);

    /**
     * 获取用户浏览记录(最多八条)
     * @param userId 用户id
     * @param houseId 房源id
     */
    List<Long> getBrowseHistoryList(Long userId, Long houseId);



}
