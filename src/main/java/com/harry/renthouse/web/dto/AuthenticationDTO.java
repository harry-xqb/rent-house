package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *  认证信息dto
 * @author Harry Xu
 * @date 2020/5/11 11:47
 */
@Data
public class AuthenticationDTO{

    @ApiModelProperty(value = "登入成功后token凭证")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private UserDTO user;
}
