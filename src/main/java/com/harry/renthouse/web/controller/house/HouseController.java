package com.harry.renthouse.web.controller.house;

import com.fasterxml.jackson.annotation.JsonView;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.entity.User;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.auth.UserService;
import com.harry.renthouse.service.house.AddressService;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.web.dto.*;
import com.harry.renthouse.web.form.SearchHouseForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Harry Xu
 * @date 2020/5/14 10:18
 */
@RestController
@Api(tags = "房源接口")
@RequestMapping("house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("houses")
    @ApiOperation(value = "按条件搜索房源")
    public ApiResponse<ServiceMultiResult> searchHouses(@RequestBody @Validated SearchHouseForm searchHouseForm){
        ServiceMultiResult<HouseDTO> result = houseService.search(searchHouseForm);
        return ApiResponse.ofSuccess(result);
    }

    @GetMapping("{id}")
    public ApiResponse<HouseCompleteInfoDetailDTO> getHouseById(@PathVariable Long id){
        HouseCompleteInfoDTO houseInfo = houseService.findCompleteHouse(id);
        HouseCompleteInfoDetailDTO result = modelMapper.map(houseInfo, HouseCompleteInfoDetailDTO.class);
        // 获取用户信息
        UserDTO agent = userService.findUserById(houseInfo.getHouse().getAdminId());
        // 获取小区出租房屋数
        int houseCountInDistrict = 10;
        // 返回结果
        result.setAgent(agent);
        result.setHouseCountInDistrict(houseCountInDistrict);
        return ApiResponse.ofSuccess(result);
    }
}
