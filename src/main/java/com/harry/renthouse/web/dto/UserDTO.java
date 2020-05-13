package com.harry.renthouse.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Set;

/**
 *  用户信息dto
 * @author Harry Xu
 * @date 2020/5/11 11:48
 */
@Data
public class UserDTO {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String name;

    @ApiModelProperty(value = "电子邮件", example = "923243595@qq.com")
    private String email;

    @ApiModelProperty(value = "手机号", example = "17879502601")
    private String phoneNumber;

    @ApiModelProperty(value = "加密后的密码", example = "$2a$10$sDAWvp8bPqOkqqOpHhWDTe8iCzYr8Qqg3Irm.iI9Dz8Bzlf7xeb/e")
    private String password;

    @ApiModelProperty(value = "用户状态, 1: 正常 0:禁用", example = "0")
    private Integer status;

    @ApiModelProperty(value = "创建时间", example = "2017-08-27T17:07:05")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "最近登录时间", example = "2017-08-27T17:07:05")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "最近更新时间", example = "2017-08-27T17:07:05")
    private LocalDateTime lastUpdateTime;

    @ApiModelProperty(value = "头像地址", example = "http://7xo6gy.com1.z0.glb.clouddn.com/99ff568bd61c744bf31185aeddf13580.png\"")
    private String avatar;

    @ApiModelProperty(value = "权限", example = "[{'authority': 'ROLE_ADMIN'}]")
    private Set<GrantedAuthority> authorities;
}
