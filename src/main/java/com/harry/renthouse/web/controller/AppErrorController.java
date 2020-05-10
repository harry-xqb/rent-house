package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import java.util.Map;

/**
 * web全局错误配置
 * @author Harry Xu
 * @date 2020/5/8 10:54
 */
@Controller
public class AppErrorController implements ErrorController {

    public static final String ERROR_PATH = "/error";

    @Autowired
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
    @ExceptionHandler(value = {Exception.class})
    public ApiResponse errorApiHandler(WebRequest req) {
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(req, false);
        int status = (int) attr.get("status");
        return ApiResponse.ofMessage(status, String.valueOf(attr.getOrDefault("message", "error")));
    }
}
