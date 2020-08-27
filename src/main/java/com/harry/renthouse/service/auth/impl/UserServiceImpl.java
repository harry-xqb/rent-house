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
import com.harry.renthouse.util.AuthenticatedUserUtil;
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

    // 重置密码令牌前缀
    private static final String RESET_PASS_WORD_TOKEN_PREFIX = "RESET:PASSWORD:TOKEN:";

    private static final int RESET_PASS_WORD_TOKEN_EXPIRE = 60 * 15;


    @Resource
    private UserRepository userRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private Gson gson;

    private static final  int REDIS_USER_TOKEN_EXPIRE = 60 * 60  * 24 * 7; // 默认缓存一周

    public static final String REDIS_USRE_ROLE_PREFIX = "role:user:";


    @Override
    public Optional<UserDTO> findUserById(Long id) {
        // 先从缓存中读取
        User user = readCache(REDIS_USER_ID_PREFIX + id);
        if(user != null){
            return Optional.of(modelMapper.map(user, UserDTO.class));
        }
        Optional<User> userOption = userRepository.findById(id);
        userOption.ifPresent(this::cache);
        return userOption.map(item -> modelMapper.map(item, UserDTO.class));
    }

    @Override
    public Optional<UserDTO> findByPhoneNumber(String phoneNumber) {
        // 先从缓存中读取
        User user = readCache(REDIS_USER_ID_PREFIX + phoneNumber);
        if(user != null){
            return Optional.of(modelMapper.map(user, UserDTO.class));
        }
        Optional<User> userOption = userRepository.findByPhoneNumber(phoneNumber);
        userOption.ifPresent(this::cache);
        return userOption.map(item -> modelMapper.map(item, UserDTO.class));
    }

    @Override
    @Transactional
    public void updateAvatar(String avatar) {
        Long userId = AuthenticatedUserUtil.getUserId();
        userRepository.updateAvatar(userId, avatar);
        // 如果存在缓存则更新缓存
        User user = readCache(REDIS_USER_ID_PREFIX + userId);
        if(user != null){
            user.setAvatar(avatar);
            cache(user);
        }
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(Long userId, UserBasicInfoForm userBasicInfoForm) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        user.setNickName(userBasicInfoForm.getNickName());
        user.setAvatar(userBasicInfoForm.getAvatar());
        user.setIntroduction(userBasicInfoForm.getIntroduction());
        userRepository.save(user);
        // 更新用户缓存
        cache(user);
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
        Set<SimpleGrantedAuthorityExtend> authorities = new HashSet<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + role.getName())));
        user.setAuthorities(authorities);

        // 更新用户缓存
        cache(user);
        // 更新角色缓存
        redisTemplate.opsForSet().add(
                REDIS_USRE_ROLE_PREFIX + user.getId(),
                roles.stream().map(Role::getName).distinct().toArray());
        redisTemplate.expire(REDIS_USRE_ROLE_PREFIX + user.getId(), RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE, TimeUnit.SECONDS);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public Optional<UserDTO> findByNickName(String nickName) {
        return  userRepository.findByNickName(nickName).map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Transactional
    public UserDTO createByPhone(String phone){
        // 判断手机号是否被注册
        userRepository.findByPhoneNumber(phone).ifPresent(user -> {
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
        cache(user);
        // 更新角色缓存
        String[] roles = new String[]{UserRoleEnum.ADMIN.getValue()};
        redisTemplate.opsForSet().add(
                REDIS_USRE_ROLE_PREFIX + user.getId(),
                roles);
        redisTemplate.expire(REDIS_USRE_ROLE_PREFIX + user.getId(), RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE, TimeUnit.SECONDS);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        User user = userRepository.findById(AuthenticatedUserUtil.getUserId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
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
        cache(user);
    }

    @Override
    public String generateResetPasswordToken(String phone) {
        userRepository.findByPhoneNumber(phone).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(RESET_PASS_WORD_TOKEN_PREFIX + token, phone, RESET_PASS_WORD_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return token;
    }

    @Override
    @Transactional
    public void resetPasswordByToken(String password, String token) {
        String phone = stringRedisTemplate.opsForValue().get(RESET_PASS_WORD_TOKEN_PREFIX + token);
        stringRedisTemplate.delete(RESET_PASS_WORD_TOKEN_PREFIX + token);
        if(StringUtils.isNotEmpty(phone)){
            throw new BusinessException(ApiResponseEnum.RESET_PASSWORD_INVALID_TOKEN);
        }
        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        String encodePassword = passwordEncoder.encode(password);
        userRepository.updatePassword(user.getId(), encodePassword);

        // 更新用户缓存
        user.setPassword(encodePassword);
        cache(user);
    }

    public void cache(User user){
        String idKey = REDIS_USER_ID_PREFIX + user.getId();
        String nameKey = REDIS_USER_NAME_PREFIX + user.getName();
        String phoneKey = REDIS_USER_PHONE_PREFIX + user.getPhoneNumber();
        String userJson = gson.toJson(user);
        stringRedisTemplate.opsForValue().set(idKey, userJson, REDIS_USER_TOKEN_EXPIRE, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(nameKey, userJson, REDIS_USER_TOKEN_EXPIRE, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(phoneKey, userJson, REDIS_USER_TOKEN_EXPIRE, TimeUnit.SECONDS);
    }
    public User readCache(String key){
        String userJSon = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(userJSon)){
            return gson.fromJson(userJSon, User.class);
        }
        return null;
    }

    public Set<SimpleGrantedAuthorityExtend> findUserRoles(long userId){
        Set<String> roleNameSet = redisTemplate.opsForSet().members(REDIS_USRE_ROLE_PREFIX + userId);
        if(CollectionUtils.isEmpty(roleNameSet)){
            roleNameSet = Optional.ofNullable(roleRepository.findRolesByUserId(userId))
                    .orElseThrow(() -> new DisabledException(ApiResponseEnum.NO_PRIORITY_ERROR.getMessage()))
                    .stream().map(Role::getName).collect(Collectors.toSet());
            redisTemplate.opsForSet().add(REDIS_USRE_ROLE_PREFIX + userId, roleNameSet.toArray());
            redisTemplate.expire(REDIS_USRE_ROLE_PREFIX + userId, RedisConfig.REDIS_CACHE_DEFAULT_EXPIRE, TimeUnit.SECONDS);
        }
        Set<SimpleGrantedAuthorityExtend> authorities = new HashSet<>();
        roleNameSet.forEach(roleName -> authorities.add(new SimpleGrantedAuthorityExtend("ROLE_" + roleName)));
        return authorities;
    }
}
