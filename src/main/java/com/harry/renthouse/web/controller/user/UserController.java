package com.harry.renthouse.web.controller.user;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.property.LimitsProperty;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.util.AuthenticatedUserUtil;
import com.harry.renthouse.util.FileUploaderChecker;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @Value("${qiniu.cdnPrefix}")
    private String cndPrefix;

    @Resource
    private LimitsProperty limitsProperty;

    @Resource
    private HouseService houseService;

    @GetMapping
    @ApiOperation("获取当前用户信息")
    public ApiResponse<UserDTO> getUserInfo(){
        Long userId = AuthenticatedUserUtil.getUserId();
        UserDTO userDTO = userService.findUserById(userId).orElseThrow(() -> new BusinessException(ApiResponseEnum.USER_NOT_FOUND));
        return ApiResponse.ofSuccess(userDTO);
    }

    @PutMapping("avatar/qiniu/{key}")
    @ApiOperation("更新头像通过七牛云key")
    public ApiResponse updateAvatar(@ApiParam(value = "图片上传的地址")  @PathVariable String key){
        String avatar = cndPrefix + key;
        userService.updateAvatar(avatar);
        return ApiResponse.ofSuccess(avatar);
    }

    @PutMapping("avatar/img")
    @ApiOperation("更新头像通过文件")
    public ApiResponse updateAvatar(@ApiParam(value = "头像图片") MultipartFile file){
        try {
            FileUploaderChecker.validTypeAndSize(limitsProperty.getAvatarTypeLimit(), file.getOriginalFilename(), limitsProperty.getAvatarSizeLimit(), file.getSize());
            QiniuUploadResult qiniuUploadResult = qiniuService.uploadFile(file.getInputStream());
            String key = qiniuUploadResult.getKey();
            String avatar = cndPrefix + key;
            userService.updateAvatar(avatar);
            return ApiResponse.ofSuccess(avatar);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponseEnum.FILE_UPLOAD_ERROR);
        }
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

    @PutMapping("basicInfo")
    @ApiOperation("更新用户基本信息")
    public ApiResponse<UserDTO> updateUser(@RequestBody UserBasicInfoForm userForm){
        Long userId = AuthenticatedUserUtil.getUserId();
        return ApiResponse.ofSuccess(userService.updateUserInfo(userId, userForm));
    }

    @PutMapping("password")
    @ApiOperation("修改用户密码")
    public ApiResponse updatePassword(@Validated @RequestBody UpdatePasswordForm updatePasswordForm){
        userService.updatePassword(updatePasswordForm.getOldPassword(), updatePasswordForm.getNewPassword());
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("avatar")
    @ApiOperation("移除用户头像")
    public ApiResponse removeAvatar(){
        userService.updateAvatar(null);
        return ApiResponse.ofSuccess();
    }

    @PostMapping("house/subscribe")
    @ApiOperation("预约房源")
    public ApiResponse subscribeHouse(@Validated @RequestBody SubscribeHouseForm subscribeHouseForm){
        houseService.addSubscribeOrder(subscribeHouseForm);
        return ApiResponse.ofSuccess();
    }

    @GetMapping("house/{houseId}/subscribe/status")
    @ApiOperation("获取当前用户对指定房源的预约状态")
    public ApiResponse<Integer> getHouseSubscribeInfo(@ApiParam("房屋id") @PathVariable Long houseId){
        Integer houseSubscribeStatus = houseService.getHouseSubscribeStatus(houseId);
        return ApiResponse.ofSuccess(houseSubscribeStatus);
    }

    @DeleteMapping("house/{subscribeId}/subscribe")
    @ApiOperation("取消预约")
    public ApiResponse cancelSubscribe(@ApiParam("预约id") @PathVariable Long subscribeId){
        houseService.cancelHouseSubscribe(subscribeId);
        return ApiResponse.ofSuccess();
    }

    @PostMapping("house/subscribes")
    @ApiOperation("获取当前用户所有预约的房源")
    public ApiResponse<ServiceMultiResult<HouseSubscribeInfoDTO>> listHouseSubscribes(
            @Validated @RequestBody ListHouseSubscribesForm listHouseSubscribesForm){
        ServiceMultiResult<HouseSubscribeInfoDTO> result = houseService.listUserHouseSubscribes(listHouseSubscribesForm);
        return ApiResponse.ofSuccess(result);
    }

    @PostMapping("house/{houseId}/star")
    @ApiOperation("收藏房源")
    public ApiResponse starHouse(@PathVariable Long houseId){
        houseService.starHouse(houseId);
        return ApiResponse.ofSuccess();
    }

    @GetMapping("house/{houseId}/operate")
    @ApiOperation("获取当前用户对房源的操作（收藏，预约）")
    public ApiResponse<UserHouseOperateDTO> getHouseOperate(@PathVariable Long houseId){
        UserHouseOperateDTO houseOperate = houseService.getHouseOperate(houseId);
        return ApiResponse.ofSuccess(houseOperate);
    }

    @PostMapping("house/star/list")
    @ApiOperation("用户收藏房源列表")
    public ApiResponse<ServiceMultiResult<HouseStarDTO>> getUserStarList(@Validated @RequestBody ListHouseStarForm starForm){
        ServiceMultiResult<HouseStarDTO> result = houseService.userStarHouseList(starForm);
        return ApiResponse.ofSuccess(result);
    }

    @DeleteMapping("house/{houseId}/star")
    @ApiOperation("取消收藏房源")
    public ApiResponse cancelStarHouse(@PathVariable Long houseId){
        houseService.deleteStarInfo(houseId);
        return ApiResponse.ofSuccess();
    }
}
