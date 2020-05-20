package com.harry.renthouse.service.search;

import com.harry.renthouse.elastic.entity.HouseElastic;

/**
 * @author Harry Xu
 * @date 2020/5/20 13:34
 */
public interface HouseElasticSearchService {

    /**
     * 根据房屋id获取房源信息,并创建索引数据
     * @param houseId 房屋id
     */
    HouseElastic save(Long houseId);

    /**
     * 根据房屋id删除索引数据
     * @param houseId
     */
    void delete(Long houseId);
}
