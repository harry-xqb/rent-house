package com.harry.renthouse.web.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *  房屋详情dto
 * @author Harry Xu
 * @date 2020/5/11 16:05
 */
@Data
public class HouseDetailDTO {

    private Long id;

    private String description;

    /* 户型介绍 */
    private String layoutDesc;

    /* 交通出行介绍 */
    private String traffic;

    /* 周边配套设施 */
    private String roundService;

    /* 出租方式, 1:整租  2: 合租 */
    private Integer rentWay;

    private String address;

    /* 地铁线路id */
    private Long subwayLineId;

    /* 地铁线路名称 */
    private String subwayLineName;

    private Long subwayStationId;

    private String subwayStationName;

    private Long houseId;
}
