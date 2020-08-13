package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 房源id集合表单
 * @author Harry Xu
 * @date 2020/8/13 10:53
 */
@Data
public class HouseIdListForm {

    @ApiModelProperty(value = "房源id集合")
    @NotNull(message = "房源集合不能为空")
    List<Long> houseIdList;
}
