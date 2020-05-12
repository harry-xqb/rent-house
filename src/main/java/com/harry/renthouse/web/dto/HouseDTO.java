package com.harry.renthouse.web.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  房源信息dto
 * @author Harry Xu
 * @date 2020/5/9 15:09
 */
@Data
public class HouseDTO {

    private Long id;

    private String title;

    private Integer price;

    private Integer area;

    private Integer room;

    private Integer floor;

    private Integer totalFloor;

    private Integer watchTimes;

    private Integer buildYear;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    private String cityEnName;

    private String regionEnName;

    private String cover;

    private Integer direction;

    /* 到地铁的距离 */
    private Integer distanceToSubway;

    /** 客厅数量 **/
    private Integer parlour;

    /* 小区名称 */
    private String district;

    /* 所属管理员id */
    private Long adminId;

    private Integer bathroom;

    /* 街道 */
    private String street;

    /* 标签 */
    private List<String> tags;

    /* 房屋照片集合 */
    private List<HousePictureDTO> housePictureList;

    /* 房屋信息 */
    private HouseDetailDTO houseDetail;

}
