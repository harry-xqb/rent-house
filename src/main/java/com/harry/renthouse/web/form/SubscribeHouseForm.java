package com.harry.renthouse.web.form;

import com.harry.renthouse.web.form.annotation.PhoneAnnotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/6/4 17:15
 */
@Data
@ApiModel("预约看房表单")
public class SubscribeHouseForm {

    @NotNull(message = "房源id不能为空")
    @ApiModelProperty("房源id")
    private Long houseId;

    @ApiModelProperty("看房时间")
    private Date time;

    @NotNull(message = "手机号不能为空")
    @PhoneAnnotation()
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("预约描述")
    private String description;
}
