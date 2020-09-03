package com.harry.renthouse.service.cache;

import com.harry.renthouse.entity.House;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.web.dto.HouseBasic;
import com.harry.renthouse.web.dto.HouseDTO;

import java.util.List;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/9/1 15:39
 */
public interface RedisHouseService {

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

    /**
     * 更新房屋缓存
     * @param house 房屋信息
     */
    void updateHouse(House house);

    /**
     * 添加房源DTO缓存
     * @param houseDTO 房源信息DTO
     */
    void addHouseDTO(HouseDTO houseDTO);

    /**
     * 添加房源DTO缓存列表
     * @param houseDTOList dto列表
     */
    void addHouseDTOList(List<HouseDTO> houseDTOList);

    /**
     * 读取单个房屋缓存
     * @param houseId 房屋id
     */
    Optional<HouseDTO> getHouseDTOById(Long houseId);

    /**
     * 读取房屋缓存列表
     * @param houseIds 房屋id集合
     */
    List<HouseDTO> getByIds(List<Long> houseIds);


    /**
     * 更新房屋标签
     * @param houseId 房屋id
     * @param tags 标签名称
     */
    void addHouseTag(Long houseId, String ...tags);

    /**
     * 移除房屋标签
     * @param houseId 房屋id
     * @param tags 标签名称
     */
    void deleteHouseTag(Long houseId, String ...tags);


    /**
     * 删除房屋图片
     * @param houseId 房屋id
     */
    void deleteHousePicture(Long houseId, Long pictureId);


}
