package com.harry.renthouse.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 房屋图片dao
 * @author Harry Xu
 * @date 2020/5/9 14:27
 */
@Entity
@Data
public class HousePicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long houseId;

    /* cdn图片url */
    private String cdnPrefix;

    private Integer width;

    private Integer height;

    /* 图片文件位置 */
    private String path;

}
