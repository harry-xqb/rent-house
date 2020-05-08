package com.harry.renthouse.security.ajax_login;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录口
 * @author Harry Xu
 * @date 2020/5/8 14:00
 */
@Component
public class AjaxAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");//设置服务器的编码，默认是ISO-8859-1
        httpServletResponse.setContentType("application/json; charset = utf-8");//告诉浏览器服务器的编码格式
        httpServletResponse.getWriter().write(new Gson().toJson(ApiResponse.ofStatus(ApiResponseEnum.NOT_LOGIN)));
    }
}