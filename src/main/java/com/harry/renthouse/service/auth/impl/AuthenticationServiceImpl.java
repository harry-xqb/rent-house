package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.controller.dto.AuthenticationDTO;
import com.harry.renthouse.controller.dto.UserDTO;
import com.harry.renthouse.security.RentHouseUserDetailService;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.util.TokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Harry Xu
 * @date 2020/5/11 11:51
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Optional<AuthenticationDTO> login(String username, String password) {
        User user = (User) rentHouseUserDetailService.loadUserByUsername(username);
        if(passwordEncoder.matches(user.getPassword(), password)){
            String token = TokenUtil.generate();
            redisUtil.set(token, user.getUsername(), TokenUtil.DEFAULT_TOKEN_EXPIRE_TIME);
            AuthenticationDTO authenticationDTO = new AuthenticationDTO();
            authenticationDTO.setToken(token);
            authenticationDTO.setUser(modelMapper.map(user, UserDTO.class));
            return Optional.of(authenticationDTO);
        }
        return Optional.empty();
    }
}
