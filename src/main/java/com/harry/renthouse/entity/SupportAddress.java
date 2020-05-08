package com.harry.renthouse.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *  支持地区
 * @author Harry Xu
 * @date 2020/5/8 16:57
 */
@Entity
@Data
public class SupportAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 上级行政单位 **/
    private String belongTo;

    private String enName;

    private String cnName;

    private String level;

    /* 百度地图经度 */
    private Long baiduMapLng;

    /* 百度地图纬度 */
    private Long baiduMapLat;

    @AllArgsConstructor
    @Getter
    public enum AddressLevel{
        PROVINCE("province"),
        CITY("city"),
        region("region");

        private String value;

        public static AddressLevel of(String value){
            for (AddressLevel level : AddressLevel.values()) {
                if(StringUtils.equals(level.getValue(), value)){
                    return level;
                }
            }
            throw  new IllegalArgumentException("无效的行政级别");
        }
    }

}
