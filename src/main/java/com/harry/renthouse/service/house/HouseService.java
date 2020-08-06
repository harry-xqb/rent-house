package com.harry.renthouse.service.house;

import com.harry.renthouse.base.HouseOperationEnum;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;

/**
 * 房屋service
 * @author Harry Xu
 * @date 2020/5/9 15:05
 */
public interface HouseService {


    /**
     * 新增房源
     * @param houseForm 房源表单
     * @return 房源信息
     */
    HouseDTO addHouse(HouseForm houseForm);

    /**
     * 房屋编辑功能
     * @param houseForm
     * @return
     */
    HouseDTO editHouse(HouseForm houseForm);

    /**
     * 管理员房源列表搜索
     * @param adminHouseSearchForm 管理员房源列表搜索
     * @return
     */
    ServiceMultiResult<HouseDTO> adminSearch(AdminHouseSearchForm adminHouseSearchForm);

    /**
     * 查找完整的房屋信息
     * @param houseId 房源id
     * @return
     */
    HouseCompleteInfoDTO findCompleteHouse(Long houseId);

    /**
     * 为房屋添加标签
     * @param tagForm 标签表单
     */
    void addTag(TagForm tagForm);

    /**
     * 为房屋删除标签
     * @param tagForm 标签表单
     */
    void deleteTag(TagForm tagForm);

    /**
     * 删除图片
     * @param pictureId 图片id
     */
    void deletePicture(Long pictureId);

    /**
     * 更新封面
     * @param coverId 封面id
     */
    void updateCover(Long coverId, Long houseId);

    /**
     * 更新房屋状态
     * @param houseId  房屋id
     * @param houseOperationEnum 操作码
     */
    void updateStatus(Long houseId, HouseOperationEnum houseOperationEnum);

    /**
     * 房源搜索
     * @param searchHouseForm 房源搜索表单
     */
    ServiceMultiResult<HouseDTO> search(SearchHouseForm searchHouseForm);

    /**
     * 城市级别搜索房源
     * @param mapSearchForm 地图搜索表单
     */
    ServiceMultiResult<HouseDTO> mapHouseSearch(MapSearchForm mapSearchForm);


    /**
     * 新增房屋预约订单
     * @param subscribeHouseForm 房屋预约表单
     */
    void addSubscribeOrder(SubscribeHouseForm subscribeHouseForm);

    /**
     * 获取房屋预约状态
     * @param houseId 房屋id
     * @return 房屋预约状态
     */
    Integer getHouseSubscribeStatus(Long houseId);

    /**
     * 获取房客预约房源列表
     * @param subscribesForm 预约信息
     */
    ServiceMultiResult<HouseSubscribeInfoDTO> listUserHouseSubscribes(ListHouseSubscribesForm subscribesForm);

    /**
     * 取消房源预约
     * @param subscribeId 预约id
     */
    void cancelHouseSubscribe(Long subscribeId);

    /**
     * 获取房东预约房源列表
     * @param subscribesForm 预约信息
     */
    ServiceMultiResult<HouseSubscribeInfoDTO> listAdminHouseSubscribes(ListHouseSubscribesForm subscribesForm);

    /**
     * 完成房屋月刊
     * @param subscribeId 约看id
     */
    void adminUpdateHouseSubscribeStatus(Long subscribeId, int status);


    /**
     * 关注房源
     * @param houseId 房屋id
     */
    void starHouse(Long houseId);

    /**
     * 用户收藏房源列表
     * @param houseStarForm 房屋收藏搜索表单
     */
    ServiceMultiResult<HouseStarDTO> userStarHouseList(ListHouseStarForm houseStarForm);

    /**
     * 用户收藏房源列表
     * @param houseId 房源id
     */
    void deleteStarInfo(Long houseId);

    /**
     *  用户是否收藏指定房屋
     * @param houseId 房屋id
     */
    UserHouseOperateDTO getHouseOperate(Long houseId);

}
