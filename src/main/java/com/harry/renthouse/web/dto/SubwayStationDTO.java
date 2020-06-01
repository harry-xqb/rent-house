package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 地铁站dto
 * @author Harry Xu
 * @date 2020/5/9 13:55
 */
@Data
@ApiModel("地铁站 DTO")
public class SubwayStationDTO {

    @ApiModelProperty(value = "地铁站id", example = "33")
    private Long id;

    @ApiModelProperty(value = "地铁线路id", example = "2")
    private Long subwayId;

    @ApiModelProperty(value = "地铁站名称", example = "萍水街")
    private String name;

}
