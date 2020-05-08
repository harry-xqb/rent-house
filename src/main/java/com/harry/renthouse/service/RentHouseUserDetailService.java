package com.harry.renthouse.service;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.Role;
import com.harry.renthouse.entity.User;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findUserByName(username));
        User user = userOptional.orElseThrow(() -> new AuthenticationCredentialsNotFoundException("用户不存在"));
        List<Role> roleList = Optional.ofNullable(roleRepository.findRolesByUserId(user.getId()))
                .orElseThrow(() -> new DisabledException(ApiResponseEnum.NO_PRIORITY_ERROR.getMessage()));
        Set<GrantedAuthority> authorities = new HashSet<>();
        roleList.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        user.setAuthorities(authorities);
        return user;
    }
}
