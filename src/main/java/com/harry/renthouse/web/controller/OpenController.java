package com.harry.renthouse.web.controller;

import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.property.LimitsProperty;
import com.harry.renthouse.web.dto.LimitsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Harry Xu
 * @date 2020/5/27 11:12
 */
@RestController
@RequestMapping("open")
@Api(tags = "开放接口")
public class OpenController {

    @Resource
    private LimitsProperty limitsProperty;

    @Resource
    private ModelMapper modelMapper;

    @GetMapping("limits")
    @ApiOperation(value = "获取请求限制条件")
    public ApiResponse<LimitsDTO> getLimits(){
        return ApiResponse.ofSuccess(modelMapper.map(limitsProperty, LimitsDTO.class));
    }
}
