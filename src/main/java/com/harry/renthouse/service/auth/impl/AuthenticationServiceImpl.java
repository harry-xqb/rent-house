package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.security.RentHouseUserDetailService;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.util.TokenUtil;
import com.harry.renthouse.web.dto.AuthenticationDTO;
import com.harry.renthouse.web.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:51
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Resource
    private UserService userService;


    @Override
    public AuthenticationDTO loginByUsername(String username, String password) {
        User user = (User)rentHouseUserDetailService.loadUserByUsername(username);
        // 如果用户不存在或者密码不匹配
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BusinessException(ApiResponseEnum.USERNAME_PASSWORD_ERROR);
        }
        return tokenGenerate(user.getUsername());
    }

    @Override
    public AuthenticationDTO loginByPhone(String phone, String password) {
        User user = (User)rentHouseUserDetailService.loadUserByPhone(phone);
        // 如果用户不存在或者密码不匹配
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BusinessException(ApiResponseEnum.USERNAME_PASSWORD_ERROR);
        }
        return tokenGenerate(user.getUsername());
    }

    @Override
    public AuthenticationDTO noPassLogin(String phone) {
        UserDTO userDTO = userService.findByPhoneNumber(phone).orElseGet(() -> userService.createByPhone(phone));
        return tokenGenerate(userDTO.getName());
    }

    @Override
    public void logout(String token) {
        tokenUtil.delete(token);
    }

    private AuthenticationDTO tokenGenerate(String username){
        // 生成token并设置过期时间
        String token = tokenUtil.generate(username);
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setToken(token);
//        authenticationDTO.setUser(modelMapper.map(user, UserDTO.class));
        return authenticationDTO;
    }
}
