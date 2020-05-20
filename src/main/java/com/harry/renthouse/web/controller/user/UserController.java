package com.harry.renthouse.web.controller.user;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.AuthenticatedUserUtil;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.web.dto.UserDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Harry Xu
 * @date 2020/5/20 16:05
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户接口")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping
    @ApiOperation("获取当前用户信息")
    public ApiResponse<UserDTO> getUserInfo(){
        Long userId = AuthenticatedUserUtil.getUserId();
        UserDTO userDTO = userService.findUserById(userId);
        return ApiResponse.ofSuccess(userDTO);
    }
}
