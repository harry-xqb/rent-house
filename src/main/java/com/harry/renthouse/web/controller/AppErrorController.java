package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * web全局错误配置
 * @author Harry Xu
 * @date 2020/5/8 10:54
 */
@Controller
public class AppErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @Resource
    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * 除Web页面外的错误处理，比如Json/XML等
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(WebRequest req, HttpServletResponse res) {
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(req, false);
        int status = (int) attr.get("status");
        String message = String.valueOf(attr.getOrDefault("message", "error"));
        if(StringUtils.endsWith(message, ApiResponseEnum.UNAUTHORIZED.getMessage())){
            status = ApiResponseEnum.UNAUTHORIZED.getCode();
            res.setStatus(status);
        }
        res.setStatus(HttpStatus.OK.value());
        return ApiResponse.ofMessage(status, message);
    }
}
