package com.harry.renthouse.security;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.util.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.util.Map;

/**
 * token认证过滤器
 * @author Harry Xu
 * @date 2020/5/11 9:52
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final static String  TOKEN_HEADER = "Authorization";

    private final static String  TOKEN_PREFIX = "Bearer ";

    private final static String  REQUEST_PARAM_TOKEN = "token";

    @Resource
    private TokenUtil tokenUtil;

    @Resource
    private RentHouseUserDetailService rentHouseUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {
        String token = "";
        String header = req.getHeader(TOKEN_HEADER);
        if(StringUtils.isNotEmpty(header)){
            //解析Token时将“Bearer ”前缀去掉
            token = StringUtils.trim(header).replace(TOKEN_PREFIX, "");
        }
        else if(StringUtils.isBlank(header)){
            token = req.getParameter(REQUEST_PARAM_TOKEN);
        }
        // 如果请求头中有token,则生成Authentication凭证
        if (StringUtils.isNotBlank(token)) {
            if (tokenUtil.hasToken(token)){
                tokenUtil.refresh(token);
                String username = tokenUtil.getUsername(token);
                User user = (User)rentHouseUserDetailService.loadUserByUsername(username);
                // 返回新的认证信息，带上 token 和反查出的用户信息
                Authentication auth = new PreAuthenticatedAuthenticationToken(user, token, user.getAuthorities());
                auth.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
//            Authentication auth = new TokenAuthentication(token);

        }
        fc.doFilter(req, res);
    }
}
