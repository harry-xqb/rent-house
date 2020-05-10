package com.harry.renthouse.controller.admin;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.controller.dto.HouseDTO;
import com.harry.renthouse.controller.form.HouseForm;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${spring.servlet.multipart.location}")
    private String fileStorePath;

    @PostMapping(value = "admin/add/house")
    public ApiResponse addHouse(@RequestBody HouseForm houseForm){
        HouseDTO houseDto = houseService.addHouse(houseForm);
        return ApiResponse.ofSuccess(houseDto);
    }

    @PostMapping(value = "admin/upload/photo")
    public ApiResponse uploadPhoto(MultipartFile file){
        String fileName = file.getOriginalFilename();
        File target = new File(fileStorePath, fileName);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponseEnum.FILE_UPLOAD_ERROR);
        }
        return ApiResponse.ofSuccess(target.getPath());
    }
}
