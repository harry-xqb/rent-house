package com.harry.renthouse.elastic.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * 房屋索引映射模板
 * @author Harry Xu
 * @date 2020/5/19 15:41
 */
@Data
@Document(indexName = "rent-house", type = "_doc", shards = 1, replicas = 0)
@ToString
public class HouseElastic {

    @Id
    private Long houseId;

    private String title;

    private Integer price;

    private Integer area;

    private Date createTime;

    private Date lastUpdateTime;

    private String cityEnName;

    private String regionEnName;

    private Integer direction;

    private Integer distanceToSubway;

    private String subwayLineName;

    private Long subwayLineId;

    private String subwayStationName;

    private Long subwayStationId;

    private String address;

    private List<String> tags;

    private String street;

    private String district;

    private String description;

    private String layoutDesc;

    private String traffic;

    private String roundService;

    private Integer rentWay;

    private List<HouseSuggestion> suggests;

    private BaiduMapLocation location;

}
