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
    @Field(type = FieldType.Long)
    private Long houseId;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Integer)
    private Integer price;

    @Field(type = FieldType.Integer)
    private Integer area;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date lastUpdateTime;

    @Field(type = FieldType.Keyword)
    private String cityEnName;

    @Field(type = FieldType.Keyword)
    private String regionEnName;

    @Field(type = FieldType.Integer)
    private Integer direction;

    @Field(type = FieldType.Integer)
    private Integer distanceToSubway;

    @Field(type = FieldType.Keyword)
    private String subwayLineName;

    @Field(type = FieldType.Keyword)
    private String subwayStationName;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private List<String> tags;

    @Field(type = FieldType.Keyword)
    private String street;

    @Field(type = FieldType.Keyword)
    private String district;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String description;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String layoutDesc;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String traffic;

    @Field(type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String roundService;

    @Field(type = FieldType.Integer)
    private Integer rentWay;

    @Field(type =  FieldType.Object, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private List<HouseSuggestion> suggests;

}
