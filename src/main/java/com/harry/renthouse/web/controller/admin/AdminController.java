package com.harry.renthouse.web.controller.admin;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.dto.QiniuUploadResult;
import com.harry.renthouse.web.form.HouseForm;
import com.harry.renthouse.service.house.HouseService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;

/**
 * @author Harry Xu
 * @date 2020/5/9 15:04
 */
@RestController
public class AdminController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private QiniuService qiniuService;

    @Autowired
    private Gson gson;

    @Value("${spring.servlet.multipart.location}")
    private String fileStorePath;

    @PostMapping(value = "admin/add/house")
    public ApiResponse addHouse(@RequestBody HouseForm houseForm){
        HouseDTO houseDto = houseService.addHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }

    @PostMapping(value = "admin/upload/photo")
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

}
