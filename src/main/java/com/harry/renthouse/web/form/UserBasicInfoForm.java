package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/5/21 18:02
 */
@Data
public class UserBasicInfoForm {

/*    @ApiModelProperty(value = "用户id")
    private Long id;*/

    @ApiModelProperty(value = "用户名称", required = true)
    @NotNull(message = "用户名称不能为空")
    private String nickName;

    @ApiModelProperty(value = "个人介绍")
    private String introduction;

}
