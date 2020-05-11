package com.harry.renthouse.service.auth;

import com.harry.renthouse.controller.dto.AuthenticationDTO;

import java.util.Optional;

/**
 * 认证服务
 * @author Harry Xu
 * @date 2020/5/11 11:46
 */
public interface AuthenticationService {

    Optional<AuthenticationDTO> login(String username, String password);
}
