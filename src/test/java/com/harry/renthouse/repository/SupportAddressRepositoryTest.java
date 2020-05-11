package com.harry.renthouse.repository;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.entity.SupportAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/11 15:07
 */
class SupportAddressRepositoryTest extends RentHouseApplicationTests {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Test
    void findAllByEnNameIn() {
        List<SupportAddress> list = supportAddressRepository.findAllByEnNameIn(Arrays.asList("bj", "dcq"));
        Assert.isTrue(list.size() == 2, "查询的区域结果不符和");
    }
}