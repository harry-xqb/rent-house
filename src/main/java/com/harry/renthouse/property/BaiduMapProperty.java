package com.harry.renthouse.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry Xu
 * @date 2020/6/1 11:42
 */
@Configuration
@ConfigurationProperties(prefix = "baidu.map")
@EnableConfigurationProperties
@Data
public class BaiduMapProperty {

    private String accessKey;

    private String geoTableId;

    /** 地理位置逆编码 **/
    private String geoLocationUrl = "http://api.map.baidu.com/geocoding/v3/?output=json&";

    /** poi创建数据 **/
    private String poiCreateUrl = "http://api.map.baidu.com/geodata/v3/poi/create";

    /** poi更新数据 **/
    private String poiUpdateUrl = "http://api.map.baidu.com/geodata/v3/poi/update";

    /** poi按条件查询数据 **/
    private String poiQueryUrl = "http://api.map.baidu.com/geodata/v3/poi/list";

    /** poi删除数据 **/
    private String poiDeleteUrl = "http://api.map.baidu.com/geodata/v3/poi/delete";

}
