package com.harry.renthouse.web.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *  房屋图片dto
 * @author Harry Xu
 * @date 2020/5/11 16:05
 */
@Data
public class HousePictureDTO {

    private Long id;

    private Long houseId;

    /* cdn图片url */
    private String cdnPrefix;

    private Integer width;

    private Integer height;

    /* 图片文件位置 */
    private String path;

}
