package com.harry.renthouse.web.controller.admin;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.security.RentHouseUserDetailService;
import com.harry.renthouse.service.auth.AuthenticationService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.util.RedisUtil;
import com.harry.renthouse.web.dto.AuthenticationDTO;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.dto.QiniuUploadResult;
import com.harry.renthouse.web.form.HouseForm;
import com.harry.renthouse.web.form.UserNamePasswordLoginForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

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
    private Gson gson;

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${spring.servlet.multipart.location}")
    private String fileStorePath;

    @PostMapping(value = "add/house")
    public ApiResponse addHouse(@RequestBody HouseForm houseForm){
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

    @PostMapping(value = "login")
    public ApiResponse login(@RequestBody UserNamePasswordLoginForm form){
        AuthenticationDTO authenticationDTO = authenticationService.adminLogin(form.getUsername(), form.getPassword());
        return ApiResponse.ofSuccess(authenticationDTO);
    }

}
