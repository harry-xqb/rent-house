package com.harry.renthouse.service.search;

import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.form.SearchHouseForm;

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
    ServiceMultiResult<Long> search(SearchHouseForm searchHouseForm);
}
