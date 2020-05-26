package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.AuthenticatedUserUtil;
import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.entity.Role;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.RoleRepository;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.web.dto.UserDTO;
import com.harry.renthouse.web.form.UserBasicInfoForm;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/18 18:40
 */
@Service
public class UserServiceImpl implements UserService {

    public static final String DEFAULT_NICk_NAME_PREFIX = "zfyh";

    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RoleRepository roleRepository;

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO findByPhoneNumber(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        return modelMapper.map(phoneNumber, UserDTO.class);
    }

    @Override
    @Transactional
    public void updateAvatar(String avatar) {
        Long userId = AuthenticatedUserUtil.getUserId();
        userRepository.updateAvatar(userId, avatar);
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserBasicInfoForm userBasicInfoForm) {
        // 判断用户昵称是否存在
        userRepository.findByPhoneNumber(userBasicInfoForm.getNickName()).ifPresent(user -> {
            throw new BusinessException(ApiResponseEnum.USER_NICK_NAME_ALREADY_EXIST);
        });
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        modelMapper.map(userBasicInfoForm, user);
        user = userRepository.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO registerUserByPhone(UserPhoneRegisterForm phoneRegisterForm, List<UserRoleEnum> roleList) {
        // 判断手机号是否被注册
        userRepository.findByPhoneNumber(phoneRegisterForm.getPhoneNumber()).ifPresent(user -> {
            throw new BusinessException(ApiResponseEnum.PHONE_ALREADY_REGISTERED);
        });
        // 执行注册用户逻辑
        User user = new User();
        user.setPhoneNumber(phoneRegisterForm.getPhoneNumber());
        user.setName(DEFAULT_NICk_NAME_PREFIX + phoneRegisterForm.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(phoneRegisterForm.getPassword()));
        user.setNickName(DEFAULT_NICk_NAME_PREFIX + phoneRegisterForm.getPhoneNumber());
        User result = userRepository.save(user);
        // 获取用户id设置角色
        Long userId = result.getId();
        List<Role> roles = roleList.stream().map(item -> {
            Role role = new Role();
            role.setName(item.getValue());
            role.setUserId(userId);
            return role;
        }).collect(Collectors.toList());
        roles = roleRepository.saveAll(roles);
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorities(authorities);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public Optional<UserDTO> findByNickName(String nickName) {
        return  userRepository.findByNickName(nickName).map(user -> modelMapper.map(user, UserDTO.class));
    }
}
