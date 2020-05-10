package com.harry.renthouse.controller.form;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/5/9 14:45
 */
@Data
public class HouseForm {

    private Long id;

    @NotBlank(message = "房源标题不能为空")
    private String title;

    @NotNull(message = "房源价格不能为空")
    private Integer price;

    @NotNull(message = "房源面积不能为空")
    private Integer area;

    @NotNull(message = "房间数量不能为空")
    private Integer room;

    @NotNull(message = "房源楼层不能为空")
    private Integer floor;

    @NotNull(message = "总楼层不能为空")
    private Integer totalFloor;

    @NotNull(message = "房源年限不能为空")
    private Integer buildYear;

    @NotNull(message = "房源城市不能为空")
    private String cityEnName;

    @NotNull(message = "房源区县不能为空")
    private String regionEnName;

    private String cover;

    @NotNull(message = "房屋朝向不能为空")
    private Integer direction;

    /* 到地铁的距离 */
    @NotNull(message = "房源到地铁距离不能为空")
    private Integer distanceToSubway;

    /** 客厅数量 **/
    @NotNull(message = "房源客厅数量不能为空")
    @Min(value = 0, message = "客厅数量非法")
    private Integer parlour;

    /* 小区名称 */
    @NotNull(message = "房源小区名称不能为空")
    private String district;

    @NotNull(message = "房源卫生间数量不能为空")
    @Min(value = 0, message = "卫生间数量非法")
    private Integer bathroom;

    /* 街道 */
    @NotNull(message = "房源街道名称不能为空")
    private String street;
}
