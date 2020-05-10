package com.harry.renthouse.web.controller.house;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.web.dto.SubwayDTO;
import com.harry.renthouse.web.dto.SubwayStationDTO;
import com.harry.renthouse.web.dto.SupportAddressDTO;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *  城市地区及地铁站控制器
 * @author Harry Xu
 * @date 2020/5/9 14:44
 */
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;


    @GetMapping("address/support/cities")
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> cities = addressService.findAllCities();
        return ApiResponse.ofSuccess(cities);
    }

    @GetMapping("address/support/regions/{cityEnName}")
    public ApiResponse getSupportRegionsByBelongTo(@PathVariable String cityEnName){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAreaByBelongToAndLevel(cityEnName, SupportAddress.AddressLevel.REGION.getValue());
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("address/support/subways/{cityEnName}")
    public ApiResponse getSubwaysByCityEnName(@PathVariable String cityEnName){
        ServiceMultiResult<SubwayDTO> result = addressService.findAllSubwayByCityEnName(cityEnName);
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("address/support/subwayStations/{subwayId}")
    public ApiResponse getSubwayStationsBySubwayId(@PathVariable Long subwayId){
        ServiceMultiResult<SubwayStationDTO> result = addressService.findAllSubwayStationBySubwayId(subwayId);
        return ApiResponse.ofSuccess(result);
    }
}
