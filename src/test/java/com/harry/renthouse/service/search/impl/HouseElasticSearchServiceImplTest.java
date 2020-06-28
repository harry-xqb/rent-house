package com.harry.renthouse.service.search.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.entity.Item;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import com.harry.renthouse.web.dto.HouseBucketDTO;
import com.harry.renthouse.web.form.SearchHouseForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/20 14:03
 */
class HouseElasticSearchServiceImplTest extends RentHouseApplicationTests {

    @Autowired
    private HouseElasticSearchService houseElasticSearchService;

    @Autowired
    private HouseRepository houseRepository;

    @Test
    void save() {
       /* List<Long> list = new ArrayList<>(Arrays.asList(15L, 16L, 17L, 18L, 19L, 20L, 21L,
                24L, 25L, 29L, 39L, 44L, 45L, 47L, 48L, 49L, 50L, 51L, 52L));*/
//        List<Long> list = new ArrayList<>(Arrays.asList(15L));
        List<Long> list = houseRepository.findAll().stream().map(House::getId).collect(Collectors.toList());
        for (Long aLong : list) {
            houseElasticSearchService.save(aLong);
        }
    }

    @Test
    void delete() {
        List<Long> list = new ArrayList<>(Arrays.asList(15L, 16L, 17L, 18L, 19L, 20L, 21L, 24L, 25L, 29L));
//        List<Long> list = new ArrayList<>(Arrays.asList(15L));
        for (Long aLong : list) {
            houseElasticSearchService.delete(aLong);
        }
//        houseElasticSearchService.delete(25L);
    }

    @Test
    void search(){
        SearchHouseForm searchHouseForm = new SearchHouseForm();
        searchHouseForm.setCityEnName("bj");
        searchHouseForm.setPageSize(10);
        searchHouseForm.setPage(1);
        ServiceMultiResult<Long> result = houseElasticSearchService.search(searchHouseForm);
        Assert.isTrue(result.getTotal() == 10, "获取的房源数量不匹配");
    }

    @Test
    void suggest(){
        ServiceMultiResult<String> result = houseElasticSearchService.suggest("富力");
        Assert.isTrue(result.getTotal() > 0, "查询数目不匹配");
    }

    @Test
    void aggregateDistrictHouse() {
        houseElasticSearchService.aggregateDistrictHouse("bj", "hdq", "融泽嘉园");
    }

    @Test
    void mapAggregateRegionsHouse() {
        ServiceMultiResult<HouseBucketDTO> result = houseElasticSearchService.mapAggregateRegionsHouse("bj");
        Assert.isTrue(result.getTotal() == 10, "总数不匹配");
        Assert.isTrue(result.getList().size() == 3, "桶的数量不匹配");
    }

}