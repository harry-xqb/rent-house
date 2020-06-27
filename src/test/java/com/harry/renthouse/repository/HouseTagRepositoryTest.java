package com.harry.renthouse.repository;

import com.harry.renthouse.RentHouseApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/6/27 16:29
 */
class HouseTagRepositoryTest extends RentHouseApplicationTests {

    @Resource
    private HouseTagRepository houseTagRepository;

    @Test
    void findALLHouseIdMatchTags() {
        List<String> list = Arrays.asList("集体供暖", "光照充足");
        List<Long> result = houseTagRepository.findALLHouseIdMatchTags(list, list.size());
        Assert.isTrue(result.size() == 1, "结果不匹配");
    }
}