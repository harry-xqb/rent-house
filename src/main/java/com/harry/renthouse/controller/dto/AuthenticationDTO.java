package com.harry.renthouse.controller.dto;

import lombok.Data;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:47
 */
@Data
public class AuthenticationDTO{

    private String token;

    private UserDTO user;
}
