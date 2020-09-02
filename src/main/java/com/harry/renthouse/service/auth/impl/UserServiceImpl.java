package com.harry.renthouse.service.auth.impl;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.SimpleGrantedAuthorityExtend;
import com.harry.renthouse.base.UserRoleEnum;
import com.harry.renthouse.config.RedisConfig;
import com.harry.renthouse.entity.Role;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.RoleRepository;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.security.RentHouseUserDetailService;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.service.cache.RedisUserService;
import com.harry.renthouse.util.AuthenticatedUserUtil;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.web.dto.UserDTO;
import com.harry.renthouse.web.form.UserBasicInfoForm;
import com.harry.renthouse.web.form.UserPhoneRegisterForm;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/18 18:40
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_NICk_NAME_PREFIX = "zfyh";

    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private RedisUserService redisUserService;

    @Override
    public Optional<UserDTO> findById(Long id) {
        // 先从缓存中读取
        Optional<UserDTO> userDTO = redisUserService.getUserById(id).map(item -> modelMapper.map(item, UserDTO.class));
        if(userDTO.isPresent()){
            return userDTO;
        }
        Optional<User> userOption = userRepository.findById(id);
        // 缓存用户信息
        userOption.ifPresent(item -> {
            redisUserService.addUser(item);
        });
        return userOption.map(item -> modelMapper.map(item, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> findByUserName(String name) {
        // 先从缓存中读取
        Optional<UserDTO> userDTO = redisUserService.getUserByName(name).map(item -> modelMapper.map(item, UserDTO.class));
        if(userDTO.isPresent()){
            return userDTO;
        }
        Optional<User> userOption = userRepository.findByName(name);
        // 缓存用户信息
        userOption.ifPresent(item -> {
            redisUserService.addUser(item);
        });
        return userOption.map(item -> modelMapper.map(item, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> findByPhoneNumber(String phoneNumber) {
        // 先从缓存中读取
        Optional<UserDTO> userDTO = redisUserService.getUserByPhoneNumber(phoneNumber).map(item -> modelMapper.map(item, UserDTO.class));
        if(userDTO.isPresent()){
            return userDTO;
        }
        Optional<User> userOption = userRepository.findByPhoneNumber(phoneNumber);
        // 缓存用户信息
        userOption.ifPresent(item -> {
            redisUserService.addUser(item);
        });
        return userOption.map(item -> modelMapper.map(item, UserDTO.class));
    }

    @Override
    @Transactional
    public void updateAvatar(String avatar) {
        Long userId = AuthenticatedUserUtil.getUserId();
        userRepository.updateAvatar(userId, avatar);
        // 如果存在缓存则更新缓存
        redisUserService.getUserById(userId).ifPresent(item -> {
            item.setAvatar(avatar);
            redisUserService.addUser(item);
        });
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserBasicInfoForm userBasicInfoForm) {
        UserDTO userDTO = findById(userId).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        User user = modelMapper.map(userDTO, User.class);
        user.setNickName(userBasicInfoForm.getNickName());
        user.setAvatar(userBasicInfoForm.getAvatar());
        user.setIntroduction(userBasicInfoForm.getIntroduction());
        userRepository.save(user);
        // 更新用户缓存
        redisUserService.addUser(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public UserDTO registerUserByPhone(UserPhoneRegisterForm phoneRegisterForm, List<UserRoleEnum> roleList) {
        // 判断手机号是否被注册
        findByPhoneNumber(phoneRegisterForm.getPhoneNumber()).ifPresent(user -> {
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
        Set<SimpleGrantedAuthorityExtend> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + role.getName())));
        user.setAuthorities(authorities);

        // 更新用户缓存
        redisUserService.addUser(user);
        // 更新角色缓存
        redisUserService.addUserRoles(user.getId(), roles.stream().map(Role::getName).distinct().toArray(String[]::new));
        return modelMapper.map(user, UserDTO.class);
    }

    @Transactional
    public UserDTO createByPhone(String phone){
        // 判断手机号是否被注册
        findByPhoneNumber(phone).ifPresent(user -> {
            throw new BusinessException(ApiResponseEnum.PHONE_ALREADY_REGISTERED);
        });
        // 执行注册用户逻辑
        User user = new User();
        user.setPhoneNumber(phone);
        user.setName(DEFAULT_NICk_NAME_PREFIX + phone);
        user.setNickName(DEFAULT_NICk_NAME_PREFIX + phone);
        user = userRepository.save(user);
        // 获取用户id设置角色
        Long userId = user.getId();
        Role role = new Role();
        role.setUserId(userId);
        role.setName(UserRoleEnum.ADMIN.getValue());
        roleRepository.save(role);
        Set<SimpleGrantedAuthorityExtend> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + role.getName()));
        user.setAuthorities(authorities);

        // 更新用户缓存
        redisUserService.addUser(user);
        // 更新角色缓存
        String[] roles = new String[]{UserRoleEnum.ADMIN.getValue()};
        redisUserService.addUserRoles(user.getId(), roles);

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        UserDTO userDTO = findById(AuthenticatedUserUtil.getUserId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        User user = modelMapper.map(userDTO, User.class);
        if(StringUtils.isNotBlank(user.getPassword())){
            if(StringUtils.isBlank(oldPassword)){
                throw new BusinessException(ApiResponseEnum.ORIGINAL_PASSWORD_EMPTY_ERROR);
            }
            if(!passwordEncoder.matches(oldPassword, user.getPassword())){
                throw new BusinessException(ApiResponseEnum.ORIGINAL_PASSWORD_ERROR);
            }
        }
        String encodePassword = passwordEncoder.encode(newPassword);
        userRepository.updatePassword(user.getId(), encodePassword);
        // 更新用户缓存
        user.setPassword(encodePassword);
        redisUserService.addUser(user);
    }

    @Override
    public String generateResetPasswordToken(String phone) {
        findByPhoneNumber(phone);
        return redisUserService.addResetPasswordToken(phone);
    }

    @Override
    @Transactional
    public void resetPasswordByToken(String password, String token) {
        String phone = redisUserService.getPhoneByResetPasswordToken(token);
        if(StringUtils.isNotEmpty(phone)){
            throw new BusinessException(ApiResponseEnum.RESET_PASSWORD_INVALID_TOKEN);
        }

        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        String encodePassword = passwordEncoder.encode(password);
        userRepository.updatePassword(user.getId(), encodePassword);

        // 更新用户缓存
        user.setPassword(encodePassword);
        redisUserService.addUser(user);
    }

    @Override
    public Set<SimpleGrantedAuthorityExtend> findUserRoles(Long id) {
        Set<String> userRoles = redisUserService.getUserRoles(id);
        // 如果缓存命中则直接返回
        Set<SimpleGrantedAuthorityExtend> authorities = new HashSet<>();
        if(!CollectionUtils.isEmpty(userRoles)){
            userRoles.forEach(roleName -> authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + roleName)));
            return authorities;
        }
        userRoles = roleRepository.findRolesByUserId(id).stream().map(Role::getName).collect(Collectors.toSet());
        userRoles.forEach(roleName -> authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + roleName)));
        // 缓存用户角色
        redisUserService.addUserRoles(id, userRoles.toArray(new String[0]));
        return authorities;
    }
}
