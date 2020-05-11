package com.harry.renthouse.web.dto;

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

    private Long id;

    private String name;

    private String email;

    private String phoneNumber;

    private String password;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime lastLoginTime;

    private LocalDateTime lastUpdateTime;

    private String avatar;

    private Set<GrantedAuthority> authorities;
}
