package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.elastic.entity.BaiduMapLocation;
import com.harry.renthouse.service.house.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/6/1 13:38
 */
class AddressServiceImplTest  extends RentHouseApplicationTests {


    @Resource
    private AddressService addressService;

    @Test
    void getBaiduMapLocation() {
        String city = "杭州";
        String address = "杭州市萧山区钱江世纪城广孚联合国际中心";
        Optional<BaiduMapLocation> location = addressService.getBaiduMapLocation(city, address);
        Assert.isTrue(location.isPresent(), "获取位置为空");
        Assert.isTrue(location.get().getLon() > 0 , "获取纬度错误");
        Assert.isTrue(location.get().getLat() > 0 , "获取经度错误");
    }
}