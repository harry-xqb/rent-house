package com.harry.renthouse.security;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.Role;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.RoleRepository;
import com.harry.renthouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 用户认证及权限
 * @author Harry Xu
 * @date 2020/5/8 14:22
 */
@Service
public class RentHouseUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByName(username).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        wrapperRole(user);
        return user;
    }

    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        wrapperRole(user);
        return user;
    }
    private void wrapperRole(User user){
        List<Role> roleList = Optional.ofNullable(roleRepository.findRolesByUserId(user.getId()))
                .orElseThrow(() -> new DisabledException(ApiResponseEnum.NO_PRIORITY_ERROR.getMessage()));
        Set<GrantedAuthority> authorities = new HashSet<>();
        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorities(authorities);
    }
}
