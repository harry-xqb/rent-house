package com.harry.renthouse.security.ajax_login;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理
 * @author Harry Xu
 * @date 2020/5/8 14:14
 */
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");//设置服务器的编码，默认是ISO-8859-1
        httpServletResponse.setContentType("application/json; charset = utf-8");//告诉浏览器服务器的编码格式
        httpServletResponse.getWriter().write(new Gson().toJson(ApiResponse.ofSuccess(authentication)));
    }
}
