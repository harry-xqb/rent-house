package com.harry.renthouse.security;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.SimpleGrantedAuthorityExtend;
import com.harry.renthouse.config.RedisConfig;
import com.harry.renthouse.entity.Role;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.RoleRepository;
import com.harry.renthouse.repository.UserRepository;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.web.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户认证及权限
 * @author Harry Xu
 * @date 2020/5/8 14:22
 */
@Service
public class RentHouseUserDetailService implements UserDetailsService {

    @Resource
    private UserService userService;

    @Resource
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从缓存中读取用户
        UserDTO userDTO = userService.findByUserName(username).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        User user = modelMapper.map(userDTO, User.class);
        wrapperRole(user);
        return user;
    }

    private void wrapperRole(User user){
        Set<SimpleGrantedAuthorityExtend> userRoles = userService.findUserRoles(user.getId());
        if(CollectionUtils.isEmpty(userRoles)){
            throw new BusinessException(ApiResponseEnum.NO_PRIORITY_ERROR);
        }
        user.setAuthorities(userRoles);
    }
}
