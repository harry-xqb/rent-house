package com.harry.renthouse.service.search;

import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.dto.HouseBucketDTO;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.form.MapBoundSearchForm;
import com.harry.renthouse.web.form.MapSearchForm;
import com.harry.renthouse.web.form.SearchHouseForm;

import javax.xml.ws.Service;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/5/20 13:34
 */
public interface HouseElasticSearchService {

    /**
     * 根据房屋id获取房源信息,并创建索引数据
     * @param houseId 房屋id
     */
    void save(Long houseId);

    /**
     * 根据房屋id删除索引数据
     * @param houseId
     */
    void delete(Long houseId);

    /**
     * 搜索房源
     * @param searchHouseForm 搜索表单
     * @return 房源id
     */
    ServiceMultiResult<HouseElastic> search(SearchHouseForm searchHouseForm);

    /**
     * 搜索建议：自动补全
     * @param prefix 关键词前缀, 默认5条
     * @return 建议结果集
     */
    ServiceMultiResult<String> suggest(String prefix);

    /**
     * 搜索建议：自动补全
     * @param prefix 关键词前缀, 默认5条
     * @param size 结果大小
     * @return 建议结果集
     */
    ServiceMultiResult<String> suggest(String prefix, int size);


    /**
     * 获取小区的房源数量
     * @param cityEnName 城市名称(英文缩写)
     * @param regionEnName 区县名称（英文缩写）
     * @param district 小区名称
     * @return 房源数量
     */
    int aggregateDistrictHouse(String cityEnName, String regionEnName, String district);

    /**
     * 通过房源id获取房屋信息
     * @param houseId 房源id
     * @return 房屋信息
     */
    Optional<HouseElastic> getByHouseId(Long houseId);

    /**
     * 聚合城市区县数据
     * @param cityEnName 城市英文简称
     * @return 每个聚合区县的房源数量
     */
    ServiceMultiResult<HouseBucketDTO> mapAggregateRegionsHouse(String cityEnName);

}
