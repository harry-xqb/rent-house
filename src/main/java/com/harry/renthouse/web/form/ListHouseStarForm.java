package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/7/27 18:24
 */
@Data
@ApiModel("查询收藏房源列表表单")
public class ListHouseStarForm {

    @ApiModelProperty(value = "页号", allowableValues = "1, 2, 3")
    @Min(value = 1, message = "页号最小为1")
    private int page = 1;

    @Min(value = 1, message = "页面大小最小为1")
    @ApiModelProperty(value = "每页大小")
    private int pageSize = 10;

    @ApiModelProperty(value = "升降序: 默认降序")
    private String sortDirection = "DESC";

    @ApiModelProperty(value = "排序字段: 创建时间")
    private String orderBy = "createTime";

}
