package com.harry.renthouse.service.search.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.elastic.entity.HouseElastic;
import com.harry.renthouse.elastic.repository.HouseElasticRepository;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.entity.HouseDetail;
import com.harry.renthouse.entity.HouseTag;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.HouseDetailRepository;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.repository.HouseTagRepository;
import com.harry.renthouse.service.search.HouseElasticSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/20 13:35
 */
@Service
public class HouseElasticSearchServiceImpl implements HouseElasticSearchService {

    @Resource
    private HouseElasticRepository houseElasticRepository;

    @Resource
    private HouseRepository houseRepository;

    @Resource
    private HouseDetailRepository houseDetailRepository;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private HouseTagRepository houseTagRepository;

    @Override
    public HouseElastic save(Long houseId) {
        HouseElastic houseElastic = new HouseElastic();
        House house = houseRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_NOT_FOUND_ERROR));
        // 映射房屋数据
        modelMapper.map(house, houseElastic);
        // 映射房屋详情
        HouseDetail houseDetail = houseDetailRepository.findByHouseId(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.HOUSE_DETAIL_NOT_FOUND_ERROR));
        modelMapper.map(houseDetail, houseElastic);
        // 映射标签信息
        List<HouseTag> tags = houseTagRepository.findAllByHouseId(houseId);
        List<String> tagList = tags.stream().map(HouseTag::getName).collect(Collectors.toList());
        houseElastic.setTags(tagList);
        // 存储至elastic中
        return houseElasticRepository.save(houseElastic);
    }

    @Override
    public void delete(Long houseId) {
        houseElasticRepository.findById(houseId).orElseThrow(() -> new BusinessException(ApiResponseEnum.ELASTIC_HOUSE_NOT_FOUND));
        houseElasticRepository.deleteById(houseId);
    }
}
