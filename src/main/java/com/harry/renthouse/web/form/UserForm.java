package com.harry.renthouse.web.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/5/21 18:02
 */
@Data
public class UserForm {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty(value = "手机号")
    private String phoneNumber;

    private String password;

    private Integer status;

    private String avatar;
}
