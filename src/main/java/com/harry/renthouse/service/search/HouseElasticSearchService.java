package com.harry.renthouse.service.search;

import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.form.SearchHouseForm;

import javax.xml.ws.Service;

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
}
