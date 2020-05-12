package com.harry.renthouse.web.controller.admin;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.AdminHouseSearchForm;
import com.harry.renthouse.web.form.HouseForm;
import com.harry.renthouse.web.form.TagForm;
import com.harry.renthouse.web.form.UserNamePasswordLoginForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @author Harry Xu
 * @date 2020/5/9 15:04
 */
@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private Gson gson;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${spring.servlet.multipart.location}")
    private String fileStorePath;

    @PostMapping(value = "house/add")
    public ApiResponse addHouse(@Validated @RequestBody HouseForm houseForm){
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if(cityAndRegion.size() != 2){
            return ApiResponse.ofStatus(ApiResponseEnum.SUPPORT_ADDRESS_ERROR);
        }
        HouseDTO houseDto = houseService.addHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }

    @PostMapping(value = "upload/photo")
    public ApiResponse uploadPhoto(MultipartFile file){
        if(file == null){
            return ApiResponse.ofStatus(ApiResponseEnum.NOT_VALID_PARAM);
        }
        Response response;
        try {
            response = qiniuService.uploadFile(file.getInputStream());
            QiniuUploadResult uploadResult = gson.fromJson(response.bodyString(), QiniuUploadResult.class);
            return ApiResponse.ofSuccess(uploadResult);
        }catch (QiniuException e){
            response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponseEnum.FILE_UPLOAD_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponseEnum.FILE_UPLOAD_ERROR);
        }
    }


    @PostMapping(value = "houses")
    public ApiResponse listHouses(@RequestBody @Validated AdminHouseSearchForm adminHouseSearchForm){
        ServiceMultiResult<HouseDTO> houseDTOServiceMultiResult = houseService.adminSearch(adminHouseSearchForm);
        return ApiResponse.ofSuccess(houseDTOServiceMultiResult);
    }

    @PostMapping("house/edit")
    public ApiResponse editHouse(@RequestBody @Validated({HouseForm.Edit.class}) HouseForm houseForm){
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if(cityAndRegion.size() != 2){
            return ApiResponse.ofStatus(ApiResponseEnum.SUPPORT_ADDRESS_ERROR);
        }
        HouseDTO houseDto = houseService.editHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }

    @GetMapping("house/{houseId}")
    public ApiResponse findHouse(@PathVariable Long houseId){
        // 获取房源信息
        HouseDTO houseDTO = houseService.findCompleteHouse(houseId);
        // 获取城市信息
        Map<SupportAddress.AddressLevel, SupportAddressDTO> cityAndRegion = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
        // 设置地铁线路信息
        Long subwayLineId = houseDTO.getHouseDetail().getSubwayLineId();
        String subWayName = houseDTO.getHouseDetail().getSubwayLineName();
        SubwayDTO subwayDTO = new SubwayDTO();
        subwayDTO.setId(subwayLineId);
        subwayDTO.setName(subWayName);
        // 设置地铁站信息
        Long subwayStationId = houseDTO.getHouseDetail().getSubwayStationId();
        String subwayStationName = houseDTO.getHouseDetail().getSubwayStationName();
        SubwayStationDTO subwayStationDTO = new SubwayStationDTO();
        subwayStationDTO.setId(subwayStationId);
        subwayStationDTO.setName(subwayStationName);

        // 返回结果
        HouseCompleteInfoDTO houseCompleteInfoDTO = new HouseCompleteInfoDTO();
        houseCompleteInfoDTO.setHouse(houseDTO);
        houseCompleteInfoDTO.setCity(cityAndRegion.get(SupportAddress.AddressLevel.CITY));
        houseCompleteInfoDTO.setRegion(cityAndRegion.get(SupportAddress.AddressLevel.REGION));
        houseCompleteInfoDTO.setSubway(subwayDTO);
        houseCompleteInfoDTO.setSubwayStation(subwayStationDTO);
        return ApiResponse.ofSuccess(houseCompleteInfoDTO);
    }

    @PostMapping("house/tag")
    public ApiResponse addTag(@RequestBody @Validated TagForm tagForm){
        houseService.addTag(tagForm);
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("house/tag")
    public ApiResponse deleteTag(@RequestBody @Validated TagForm tagForm){
        houseService.deleteTag(tagForm);
        return ApiResponse.ofSuccess();
    }

    @DeleteMapping("house/picture/{pictureId}")
    public ApiResponse deletePicture(@PathVariable Long pictureId){
        houseService.deletePicture(pictureId);
        return ApiResponse.ofSuccess();
    }

    @PostMapping(value = "login")
    public ApiResponse login(@Validated @RequestBody UserNamePasswordLoginForm form){
        AuthenticationDTO authenticationDTO = authenticationService.adminLogin(form.getUsername(), form.getPassword());
        return ApiResponse.ofSuccess(authenticationDTO);
    }
}
