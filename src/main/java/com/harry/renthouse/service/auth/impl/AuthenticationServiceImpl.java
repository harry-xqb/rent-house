package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.security.RentHouseUserDetailService;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.util.TokenUtil;
import com.harry.renthouse.web.dto.AuthenticationDTO;
import com.harry.renthouse.web.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:51
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Autowired
    private ModelMapper modelMapper;

    public static final String ROLE_ADMIN = "ADMIN";

    public static final String ROLE_USER = "USER";

    @Override
    public AuthenticationDTO login(String username, String password, String role) {
        User user = (User)rentHouseUserDetailService.loadUserByUsername(username);
        // 如果用户不存在或者密码不匹配
        if(user == null || !passwordEncoder.matches(password, user.getPassword())){
            throw new BusinessException(ApiResponseEnum.USERNAME_PASSWORD_ERROR);
        }
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        boolean hasRole = authorities.stream().anyMatch(grantedAuthority -> StringUtils.equals("ROLE_" + role, grantedAuthority.getAuthority()));
        // 如果没有权限
        if(!hasRole){
            throw new BusinessException(ApiResponseEnum.NO_PRIORITY_ERROR);
        }
        // 生成token并设置过期时间
        String token = tokenUtil.generate(user.getUsername());
        AuthenticationDTO authenticationDTO = new AuthenticationDTO();
        authenticationDTO.setToken(token);
        authenticationDTO.setUser(modelMapper.map(user, UserDTO.class));
        return authenticationDTO;
    }

    @Override
    public AuthenticationDTO adminLogin(String username, String password) {
        return login(username, password, ROLE_ADMIN);
    }

    @Override
    public AuthenticationDTO userLogin(String username, String password) {
        return login(username, password, ROLE_USER);
    }
}
