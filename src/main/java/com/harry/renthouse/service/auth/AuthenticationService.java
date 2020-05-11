package com.harry.renthouse.service.auth;

import com.harry.renthouse.web.dto.AuthenticationDTO;

import java.util.Optional;

/**
 * 认证服务
 * @author Harry Xu
 * @date 2020/5/11 11:46
 */
public interface AuthenticationService {

    AuthenticationDTO login(String username, String password, String role);

    AuthenticationDTO adminLogin(String username, String password);

    AuthenticationDTO userLogin(String username, String password);
}
