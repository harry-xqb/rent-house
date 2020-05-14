package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.base.RentWayEnum;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.form.SearchHouseForm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/14 14:57
 */
@Slf4j
class HouseServiceImplTest extends RentHouseApplicationTests {

    @Autowired
    private HouseService houseService;

    @Test
    void search() {
        // 全部条件加上，只保留两个
        SearchHouseForm searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        // 查城市并且状态正常的
        ServiceMultiResult<HouseDTO> search = houseService.search(searchHouseForm);
        Assert.isTrue(search.getTotal() == 9, "查城市并且状态正常的数量不匹配");
        // 查5个并且按照价格降序
        searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setPageSize(5);
        searchHouseForm.setOrderBy("price");
        searchHouseForm.setSortDirection("DESC");
        ServiceMultiResult<HouseDTO> search1 = houseService.search(searchHouseForm);
        Assert.isTrue(search1.getList().size() == 5 && search1.getList().get(0).getPrice() == 50000, "查5个并且按照价格降序不匹配");
        // 查价格区间并且在3000元以下,2000元以上的
        searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setPriceMax(3000);
        searchHouseForm.setPriceMin(2000);
        ServiceMultiResult<HouseDTO> search2 = houseService.search(searchHouseForm);
        Assert.isTrue(search2.getTotal() == 2, "查价格区间并且在3000元以下,2000元以上的不匹配");
        // 查租房方式为整租的
        searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setRentWay(RentWayEnum.WHOLE.getValue());
        ServiceMultiResult<HouseDTO> search3 = houseService.search(searchHouseForm);
        Assert.isTrue(search3.getTotal() == 2, "查租房方式为整租的的不匹配");
        // 查找标签为独立卫生间的
        searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setTags(Collections.singletonList("独立卫生间"));
        ServiceMultiResult<HouseDTO> search4 = houseService.search(searchHouseForm);
        Assert.isTrue(search4.getTotal() == 2, "查找标签为独立卫生间的不匹配");
        // 查找区县为cpq的
        searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setRegionEnName("cpq");
        ServiceMultiResult<HouseDTO> search5 = houseService.search(searchHouseForm);
        Assert.isTrue(search5.getTotal() == 1, "查找区县为cpq的不匹配");
    }
}