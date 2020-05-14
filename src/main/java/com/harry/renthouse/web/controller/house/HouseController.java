package com.harry.renthouse.web.controller.house;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.form.SearchHouseForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harry Xu
 * @date 2020/5/14 10:18
 */
@RestController
@Api(tags = "房源接口")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @PostMapping("search/houses")
    @ApiOperation(value = "按条件搜索房源")
    public ApiResponse<ServiceMultiResult> searchHouses(@RequestBody @Validated SearchHouseForm searchHouseForm){
        ServiceMultiResult<HouseDTO> result = houseService.search(searchHouseForm);
        return ApiResponse.ofSuccess(result);
    }
}
