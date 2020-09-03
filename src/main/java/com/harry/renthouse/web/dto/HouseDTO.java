package com.harry.renthouse.web.dto;

import com.harry.renthouse.elastic.entity.BaiduMapLocation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;

/**
 *  房源信息dto
 * @author Harry Xu
 * @date 2020/5/9 15:09
 */
@Data
@ToString
@ApiModel("房屋信息")
public class HouseDTO extends HouseBasic {

    @ApiModelProperty("地理位置坐标")
    private BaiduMapLocation location;

    @ApiModelProperty("该房屋被收藏次数")
    private int starNumber;

    /* 标签 */
    @ApiModelProperty(value = "标签", example = "['交通便利', '精装修']")
    private Set<String> tags = new HashSet<>();

    /* 房屋照片集合 */
    @ApiModelProperty(value = "房屋照片集合", example = "[{'path': 'Fn6szUiUydhr3XE5xF55XCDvlc2E', 'width': 50}, 'height': 50]")
    private List<HousePictureDTO> housePictureList = new ArrayList<>();

    /* 房屋信息 */
    @ApiModelProperty("房屋详细信息")
    private HouseDetailDTO houseDetail;

}
