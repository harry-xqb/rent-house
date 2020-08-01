package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/7/27 16:54
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("房源预约信息DTO")
public class HouseStarDTO {

    private Long id;

    private HouseDTO house;

    private Date createTime;

}
