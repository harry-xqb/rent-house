package com.harry.renthouse.service.house;

import com.harry.renthouse.base.HouseOperationEnum;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.dto.HouseCompleteInfoDTO;
import com.harry.renthouse.web.dto.HouseDTO;
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
     * 根据视野查询房源
     * @param mapBoundSearchForm 视野查询表单
     */
    ServiceMultiResult<HouseDTO> mapBoundSearch(MapBoundSearchForm mapBoundSearchForm);
}
