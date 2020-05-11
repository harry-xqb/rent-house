package com.harry.renthouse.service.house;

import com.harry.renthouse.web.dto.SubwayDTO;
import com.harry.renthouse.web.dto.SubwayStationDTO;
import com.harry.renthouse.web.dto.SupportAddressDTO;
import com.harry.renthouse.service.ServiceMultiResult;

/**
 *  支持的地区service
 * @author Harry Xu
 * @date 2020/5/8 17:10
 */
public interface AddressService {

    /**
     * 查找所有城市
     * @return 行政单位列表
     */
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    /**
     *  通过所属单位与当前层级查找区域列表
     * @param belongTo 所属单位
     * @param level 行政单位级别
     * @return  行政单位列表
     */
    ServiceMultiResult<SupportAddressDTO> findAreaByBelongToAndLevel(String belongTo, String level);

    /**
     * 通过城市英文名获取所有地铁线路
     * @param cityEnName 城市英文名
     * @return 地铁线路列表
     */
    ServiceMultiResult<SubwayDTO> findAllSubwayByCityEnName(String cityEnName);

    /**
     * 通过地铁线路id查找地铁站
     * @param subwayId 地铁站id
     * @return 地铁站列表
     */
    ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId);
}
