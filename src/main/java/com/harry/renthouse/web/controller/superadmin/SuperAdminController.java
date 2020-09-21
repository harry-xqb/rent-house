package com.harry.renthouse.web.controller.superadmin;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.HouseOperationEnum;
import com.harry.renthouse.base.HouseStatusEnum;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.service.auth.SuperAdminService;
import com.harry.renthouse.service.house.HouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;

/**
 * @author Harry Xu
 * @date 2020/9/4 10:30
 */
@RestController
@RequestMapping("superAdmin")
@Api(tags = "系统管理员接口")
public class SuperAdminController {

    @Resource
    private SuperAdminService superAdminService;

    @Resource
    private HouseService houseService;

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

    @PutMapping("house/status/{id}/{status}")
    @ApiOperation("修改房屋状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "房屋id", required = true),
            @ApiImplicitParam(name = "status", value = "操作类型(0: 下架, 1: 审核通过  2: 出租  3: 删除)", required = true,
                    example = "1", allowableValues = "0,1,2,3"),
    })
    public ApiResponse changeHouseStatus(@PathVariable long id, @PathVariable int status){
        HouseOperationEnum statusEnum = HouseOperationEnum.of(status);
        houseService.updateStatus(id, statusEnum);
        return ApiResponse.ofSuccess();
    }
}
