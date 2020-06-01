package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *  房屋图片dto
 * @author Harry Xu
 * @date 2020/5/11 16:05
 */
@Data
@ApiModel("房屋图片 DTO")
public class HousePictureDTO {

    @ApiModelProperty(value = "图片id", example = "92")
    private Long id;

    @ApiModelProperty(value = "房屋id", example = "29")
    private Long houseId;

    /* cdn图片url */
    @ApiModelProperty(value = "图片cdn前缀", example = "http://qa22ygxo8.bkt.clouddn.com/")
    private String cdnPrefix;

    @ApiModelProperty(value = "宽度", example = "50")
    private Integer width;

    @ApiModelProperty(value = "高度", example = "50")
    private Integer height;

    /* 图片文件位置 */
    @ApiModelProperty(value = "图片路径", example = "Fn6szUiUydhr3XE5xF55XCDvlc2E")
    private String path;

}
