package com.harry.renthouse.web.controller.user;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.AuthenticatedUserUtil;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.web.dto.QiniuUploadResult;
import com.harry.renthouse.web.dto.UserDTO;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

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

    @Resource
    private QiniuService qiniuService;

    @Resource
    private Gson gson;

    @GetMapping
    @ApiOperation("获取当前用户信息")
    public ApiResponse<UserDTO> getUserInfo(){
        Long userId = AuthenticatedUserUtil.getUserId();
        UserDTO userDTO = userService.findUserById(userId);
        return ApiResponse.ofSuccess(userDTO);
    }

    @PutMapping("{avatar}")
    @ApiOperation("更新头像")
    public ApiResponse updateAvatar(@ApiParam(value = "图片上传的地址")  @PathVariable String avatar){
        userService.updateAvatar(avatar);
        return ApiResponse.ofSuccess();
    }

    @PostMapping(value = "upload/photo")
    @ApiOperation(value = "上传图片接口")
    public ApiResponse<QiniuUploadResult> uploadPhoto(@ApiParam(value = "图片文件") MultipartFile file){
        if(file == null){
            return ApiResponse.ofStatus(ApiResponseEnum.NOT_VALID_PARAM);
        }
        try {
           return ApiResponse.ofSuccess(qiniuService.uploadFile(file.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponseEnum.FILE_UPLOAD_ERROR);
        }
    }

}
