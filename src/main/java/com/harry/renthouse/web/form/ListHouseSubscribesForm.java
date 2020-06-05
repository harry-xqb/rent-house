package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/6/5 10:16
 */
@Data
@ApiModel("查询预约房源列表表单")
public class ListHouseSubscribesForm {

    @ApiModelProperty(value = "预约状态, 1:待看 2:已预约 3:已完成", allowableValues = "1, 2, 3")
    @NotNull(message = "预约状态不能为空")
    private int status;

    @ApiModelProperty(value = "页号", allowableValues = "1, 2, 3")
    @Min(value = 1, message = "页号最小为1")
    private int page = 1;

    @Min(value = 1, message = "页面大小最小为1")
    @ApiModelProperty(value = "每页大小")
    private int pageSize = 10;

    @ApiModelProperty(value = "升降序: 默认降序")
    private String sortDirection = "DESC";

    @ApiModelProperty(value = "排序字段:默认创建时间")
    private String orderBy = "createTime";
}
