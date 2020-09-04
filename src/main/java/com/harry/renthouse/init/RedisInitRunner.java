package com.harry.renthouse.init;

import com.harry.renthouse.entity.HouseStar;
import com.harry.renthouse.repository.HouseStarRepository;
import com.harry.renthouse.repository.SupportAddressRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/9/4 10:24
 */
@Component
public class RedisInitRunner implements ApplicationRunner {

    @Resource
    private HouseStarRepository houseStarRepository;

    @Resource
    private SupportAddressRepository supportAddressRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

}
