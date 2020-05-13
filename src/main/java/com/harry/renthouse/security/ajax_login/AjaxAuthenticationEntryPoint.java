package com.harry.renthouse.security.ajax_login;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * 登录口
 * @author Harry Xu
 * @date 2020/5/8 14:00
 */
@Component
public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        RequestCache requestCache = new HttpSessionRequestCache();
        Optional<SavedRequest> savedRequest = Optional.ofNullable(requestCache.getRequest(request, response));
        savedRequest.ifPresent(req -> {
            if(StringUtils.endsWith(req.getRedirectUrl(), ".html")){
                /*try {
                    redirectStrategy.sendRedirect(request,response, "/login.html");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }*/
                return;
            }
        });
        response.setCharacterEncoding("UTF-8");//设置服务器的编码，默认是ISO-8859-1
        response.setContentType("application/json; charset = utf-8");//告诉浏览器服务器的编码格式
        response.getWriter().write(new Gson().toJson(ApiResponse.ofStatus(ApiResponseEnum.NOT_LOGIN)));
    }
}