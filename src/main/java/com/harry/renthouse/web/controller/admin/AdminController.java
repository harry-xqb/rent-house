package com.harry.renthouse.web.controller.admin;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.HouseOperationEnum;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Harry Xu
 * @date 2020/5/9 15:04
 */
@RestController
@RequestMapping("admin")
@Api (tags = "管理员接口")
public class AdminController {

    @Resource
    private HouseService houseService;

    @Resource
    private QiniuService qiniuService;

    @Resource
    private AddressService addressService;

    @ApiOperation(value = "新增房源接口")
    @PostMapping(value = "house/add")
    public ApiResponse<HouseDTO> addHouse(@Validated @RequestBody HouseForm houseForm){
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if(cityAndRegion.size() != 2){
            return ApiResponse.ofStatus(ApiResponseEnum.SUPPORT_ADDRESS_ERROR);
        }
        HouseDTO houseDto = houseService.addHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }


    @PostMapping(value = "houses")
    @ApiOperation(value = "分页查找房源")
    public ApiResponse<ServiceMultiResult> listHouses(@RequestBody @Validated AdminHouseSearchForm adminHouseSearchForm){
        ServiceMultiResult<HouseDTO> houseDTOServiceMultiResult = houseService.adminSearch(adminHouseSearchForm);
        return ApiResponse.ofSuccess(houseDTOServiceMultiResult);
    }

    @PutMapping("house/edit")
    @ApiOperation(value = "房源信息编辑")
    public ApiResponse<HouseDTO> editHouse(@RequestBody @Validated({HouseForm.Edit.class}) HouseForm houseForm){
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if(cityAndRegion.size() != 2){
            return ApiResponse.ofStatus(ApiResponseEnum.SUPPORT_ADDRESS_ERROR);
        }
        HouseDTO houseDto = houseService.editHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }

    @GetMapping("house/{id}")
    @ApiOperation(value = "通过房屋id获取房屋信息")
    public ApiResponse<HouseCompleteInfoDTO> findHouse(@PathVariable @ApiParam(value = "房屋id", required = true) Long id){
        // 获取房源信息
        return ApiResponse.ofSuccess(houseService.findCompleteHouse(id));
    }

    @PostMapping("house/tag")
    @ApiOperation("添加标签")
    public ApiResponse addTag(@RequestBody @Validated TagForm tagForm){
        houseService.addTag(tagForm);
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("house/tag")
    @ApiOperation("删除标签")
    public ApiResponse deleteTag(@RequestBody @Validated TagForm tagForm){
        houseService.deleteTag(tagForm);
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("house/picture/{pictureId}")
    @ApiOperation("删除图片")
    public ApiResponse deletePicture(@ApiParam(value = "图片id", required = true) @PathVariable long pictureId){
        houseService.deletePicture(pictureId);
        return ApiResponse.ofSuccess();
    }

    @PutMapping("house/cover")
    @ApiOperation("更新封面")
    public ApiResponse updateCover(@RequestBody @Validated CoverForm coverForm){
        houseService.updateCover(coverForm.getCoverId(), coverForm.getHouseId());
        return ApiResponse.ofSuccess();
    }

    @PutMapping("house/operate/{id}/{operation}")
    @ApiOperation("修改房屋状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "房屋id", required = true),
            @ApiImplicitParam(name = "operation", value = "操作类型(0: 下架, 1: 审核通过  2: 出租  3: 删除)", required = true,
                    example = "1", allowableValues = "0,1,2,3"),
    })
    public ApiResponse operateHouse(@PathVariable long id, @PathVariable int operation){
        HouseOperationEnum houseOperationEnum = HouseOperationEnum.of(operation);
        houseService.updateStatus(id, houseOperationEnum);
        return ApiResponse.ofSuccess();
    }

}
