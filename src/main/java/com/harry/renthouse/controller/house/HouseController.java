package com.harry.renthouse.controller.house;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  房屋业务相关控制器
 * @author Harry Xu
 * @date 2020/5/8 17:07
 */
@RestController
public class HouseController {

    @Autowired
    private AddressService addressService;

    @GetMapping("address/support/cities")
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> cities = addressService.findAllCities();
        return ApiResponse.ofSuccess(cities);
    }
}
