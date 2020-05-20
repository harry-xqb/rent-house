package com.harry.renthouse.elastic.entity;

/**
 * @author Harry Xu
 * @date 2020/5/20 10:04
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Document(indexName = "item", shards = 1,replicas = 0)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {
    @Id   //注意此处的@Id必须为springframework包下面的id   import org.springframework.data.annotation.Id;
    Long id;
    @Field(type = FieldType.Text)
    String title; //标题
    @Field(type = FieldType.Keyword)
    String category;// 分类
    @Field(type = FieldType.Keyword)
    String brand; // 品牌
    @Field(type = FieldType.Double)
    Double price; // 价格
    @Field(type = FieldType.Keyword, index = false)
    String images; // 图片地址
}
