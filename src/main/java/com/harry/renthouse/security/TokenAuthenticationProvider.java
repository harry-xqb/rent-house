package com.harry.renthouse.security;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 直接在filter拦截token, 无需在provider中进行
 * token认证提供者
 */
@Component
@Deprecated
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (authentication.isAuthenticated()) {
            return authentication;
        }
        // 从 TokenAuthentication 中获取 token
        String token = authentication.getCredentials().toString();
        if (!tokenUtil.hasToken(token)){
            //res.setStatus(HttpStatus.UNAUTHORIZED.value());
            throw new BusinessException(ApiResponseEnum.UNAUTHORIZED);
        }
        tokenUtil.refresh(token);
        String username = tokenUtil.getUsername(token);
        User user = (User)rentHouseUserDetailService.loadUserByUsername(username);
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