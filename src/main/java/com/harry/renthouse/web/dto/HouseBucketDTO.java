package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Harry Xu
 * @date 2020/6/1 10:25
 */
@Data
@AllArgsConstructor
@ApiModel("聚合区县返回结果")
public class HouseBucketDTO {

    @ApiModelProperty(value = "聚合区县名称")
    private String region;

    @ApiModelProperty(value = "房源数量")
    private long count;
}
