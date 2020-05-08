package com.harry.renthouse.security.ajax_login;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理
 * @author Harry Xu
 * @date 2020/5/8 14:13
 */
@Component
public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");//设置服务器的编码，默认是ISO-8859-1
        httpServletResponse.setContentType("application/json; charset = utf-8");//告诉浏览器服务器的编码格式
        String message = e.getMessage();
        httpServletResponse.getWriter().write(new Gson().toJson(ApiResponse.ofMessage(ApiResponseEnum.BAD_REQUEST.getCode(), message)));
    }
}