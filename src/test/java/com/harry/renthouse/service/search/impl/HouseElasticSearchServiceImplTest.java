package com.harry.renthouse.service.search.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/20 14:03
 */
class HouseElasticSearchServiceImplTest extends RentHouseApplicationTests {

    @Autowired
    private HouseElasticSearchService houseElasticSearchService;

    @Test
    void save() {
        houseElasticSearchService.save(25L);
    }

    @Test
    void delete() {
        houseElasticSearchService.delete(25L);
    }
}