package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.util.List;

/**
 * 房屋表单
 * @author Harry Xu
 * @date 2020/5/9 14:45
 */
@Data
@ApiModel("房屋新增与编辑表单")
public class HouseForm {

    public interface Edit  extends Default {};

    @NotNull(groups = {Edit.class}, message = "房屋id不能为空" )
    @ApiModelProperty(value = "房源id:编辑房源时必传", example = "1", notes = "编辑房源时必传")
    private Long id;

    @NotBlank(message = "房源标题不能为空")
    @ApiModelProperty(value = "房源标题", required = true, example = "阮家桥公寓")
    private String title;

    @NotNull(message = "房源价格不能为空")
    @ApiModelProperty(value = "房源价格", required = true, example = "3000")
    private Integer price;

    @NotNull(message = "房源面积不能为空")
    @ApiModelProperty(value = "房源面积", required = true, example = "20")
    private Integer area;

    @NotNull(message = "房间数量不能为空")
    @ApiModelProperty(value = "房间数量", required = true, example = "1")
    private Integer room;

    @NotNull(message = "房源楼层不能为空")
    @ApiModelProperty(value = "楼层数量", required = true, example = "12")
    private Integer floor;

    @NotNull(message = "总楼层不能为空")
    @ApiModelProperty(value = "总楼层数量", required = true, example = "18")
    private Integer totalFloor;

    @NotNull(message = "建房年份不能为空")
    @ApiModelProperty(value = "建房年份", required = true, example = "2020")
    private Integer buildYear;

    @NotNull(message = "房源城市不能为空")
    @ApiModelProperty(value = "房源所属城市英文简称", required = true, example = "bj")
    private String cityEnName;

    @NotNull(message = "房源区县不能为空")
    @ApiModelProperty(value = "房源所属区县英文简称", required = true, example = "dcq")
    private String regionEnName;

    @ApiModelProperty(value = "房源封面:点击图片上传后返回的图片key/hash", example = "Fn6szUiUydhr3XE5xF55XCDvlc2E", notes = "点击图片上传后返回的图片key/hash")
    @NotNull(message = "房屋封面不能为空")
    private String cover;

    @NotNull(message = "房屋朝向不能为空")
    @ApiModelProperty(value = "房屋朝向:1,2,3,4 -> 东南西北", required = true, example = "2", allowableValues = "1,2,3,4", notes = "1,2,3,4 -> 东南西北")
    private Integer direction;

    /* 到地铁的距离 */
//    @NotNull(message = "房源到地铁距离不能为空")
    @ApiModelProperty(value = "到地铁站距离:没有距离的话传 -1", required = true, example = "500", notes = "没有距离的话传 -1")
    private Integer distanceToSubway = -1;

    /** 客厅数量 **/
    @NotNull(message = "房源客厅数量不能为空")
    @Min(value = 0, message = "客厅数量非法")
    @ApiModelProperty(value = "客厅数量;最小为0", required = true, example = "1", notes = "最小为0")
    private Integer parlour;

    /* 小区名称 */
    @NotNull(message = "房源小区名称不能为空")
    @ApiModelProperty(value = "小区名称", required = true, example = "阮家桥公寓")
    private String district;

    @NotNull(message = "房源卫生间数量不能为空")
    @Min(value = 0, message = "卫生间数量非法")
    @ApiModelProperty(value = "卫生间数量：最小为0", required = true, example = "1", notes = "最小为0")
    private Integer bathroom;

    /* 街道 */
    @NotNull(message = "房源街道名称不能为空")
    @ApiModelProperty(value = "街道名称", example = "祥符街道")
    private String street;

    /************* 房屋详情 *******************/
    @ApiModelProperty(value = "房屋描述", example = "这个人很懒，没有条件描述")
    private String description;

    /* 户型介绍 */
    @ApiModelProperty(value = "户型介绍", example = "三室一厅带独卫")
    private String layoutDesc;

    /* 交通出行介绍 */
    @ApiModelProperty(value = "交通出行介绍", example = "交通便利，近萍水街地铁站")
    private String traffic;

    /* 周边配套设施 */
    @ApiModelProperty(value = "周边配套设施", example = "小区内游泳池，免费健身器材，距银泰百货不到500米")
    private String roundService;

    /* 出租方式, 1:整租  2: 合租 */
    @NotNull(message = "出租方式不能为空")
    @ApiModelProperty(value = "租房方式： 1: 整租 2: 合租", required = true, example = "1", allowableValues = "1,2", notes = "1: 整租 2: 合租")
    private Integer rentWay;

    @NotNull(message = "房源地址不能为空")
    @ApiModelProperty(value = "房源地址", required = true, example = "拱墅区阮家桥公寓")
    private String address;

    /* 地铁线路id */
    @ApiModelProperty(value = "地铁线路id：需要使用接口查询出的地铁线路id", example = "4", notes = "需要使用接口查询出的地铁线路id")
    private Long subwayLineId;

    /* 地铁线路名称 */
    @ApiModelProperty(value = "地铁线路名称", example = "10号线")
    private String subwayLineName;

    @ApiModelProperty(value = "地铁站id：需要使用接口查询出的地铁站id", example = "7", notes = "需要使用接口查询出的地铁站id")
    private Long subwayStationId;

    @ApiModelProperty(value = "地铁站名称", example = "龙泽站")
    private String subwayStationName;

    /************ 房屋标签 *****************/
    @ApiModelProperty(value = "房屋标签", example = "['精装修', '交通便利']")
    private List<String> tags;

    /* 房屋照片 */
    @ApiModelProperty(value = "房屋照片集合: 该集合对象为点击图片上传后，后端返回的对象",
            example = "[{'path': 'Fn6szUiUydhr3XE5xF55XCDvlc2E', 'width': 50, 'height': 50}]",
            notes = "该集合对象为点击图片上传后，后端返回的对象")
    @NotNull(message = "房屋图片不能为空")
    @Size(min = 1, message = "最少上传1张图片")
    private List<PictureForm> pictures;

}
