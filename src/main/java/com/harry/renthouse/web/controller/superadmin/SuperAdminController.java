package com.harry.renthouse.web.controller.superadmin;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.service.auth.SuperAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

/**
 * @author Harry Xu
 * @date 2020/9/4 10:30
 */
@RestController
@RequestMapping("superAdmin")
public class SuperAdminController {

    @Resource
    private SuperAdminService superAdminService;

    @GetMapping("hello")
    public ApiResponse hello(){
        return ApiResponse.ofSuccess("hello super admin");
    }

    @PostMapping("/syncStarToRedis")
    @ApiOperation("同步数据库收藏数据到redis")
    public ApiResponse syncStarToRedis(){
        superAdminService.syncStarToRedisFromDatabase();
        return ApiResponse.ofSuccess();
    }
    @PostMapping("/syncStarToDatabase")
    @ApiOperation("同步redis收藏数据到数据库")
    public ApiResponse syncStarToDatabase(){
        superAdminService.syncStarToDatabaseFromRedis();
        return ApiResponse.ofSuccess();
    }
}
