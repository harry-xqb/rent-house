package com.harry.renthouse.service.auth;

import com.harry.renthouse.web.dto.AuthenticationDTO;

import java.util.Optional;

/**
 * 认证服务
 * @author Harry Xu
 * @date 2020/5/11 11:46
 */
public interface AuthenticationService {

    AuthenticationDTO loginByUsername(String username, String password);

    AuthenticationDTO loginByPhone(String phone, String password);

    AuthenticationDTO noPassLogin(String phone);
}
