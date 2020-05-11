package com.harry.renthouse.security;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * token认证提供者
 */
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.isAuthenticated()) {
            return authentication;
        }
        // 从 TokenAuthentication 中获取 token
        String token = authentication.getCredentials().toString();
        if (StringUtils.isNotBlank(token)) {
            return authentication;
        }
        if (!redisUtil.hasKey(token)) {
            throw new BusinessException(ApiResponseEnum.NOT_VALID_CREDENTIAL);
        }
        String username = (String) redisUtil.get(token);
        User user = (User) rentHouseUserDetailService.loadUserByUsername(username);
        // 返回新的认证信息，带上 token 和反查出的用户信息
        Authentication auth = new PreAuthenticatedAuthenticationToken(user, token, user.getAuthorities());
        auth.setAuthenticated(true);
        return auth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (TokenAuthentication.class.isAssignableFrom(aClass));
    }
}