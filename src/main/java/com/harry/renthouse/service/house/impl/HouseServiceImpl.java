package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.base.AuthenticatedUserUtil;
import com.harry.renthouse.entity.*;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.repository.*;
import com.harry.renthouse.service.house.HouseService;
import com.harry.renthouse.web.dto.HouseDTO;
import com.harry.renthouse.web.dto.HouseDetailDTO;
import com.harry.renthouse.web.dto.HousePictureDTO;
import com.harry.renthouse.web.form.HouseForm;
import com.harry.renthouse.web.form.PhotoForm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/9 15:12
 */
@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Value("${qiniu.cdnPrefix}")
    private String cdnPrefix;

    @Transactional
    @Override
    public HouseDTO addHouse(HouseForm houseForm) {
        // 房屋详情数据填充与校验
        HouseDetail houseDetail = generateHouseDetail(houseForm);
        // 新增房屋信息
        House house = modelMapper.map(houseForm, House.class);
        house.setAdminId(AuthenticatedUserUtil.getUserId());
        house = houseRepository.save(house);
        //新增房屋详情信息
        houseDetail.setHouseId(house.getId());
        houseDetail = houseDetailRepository.save(houseDetail);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
        // 新增房屋图片信息
        List<HousePicture> housePictures = housePictureRepository.saveAll(generateHousePicture(houseForm, house.getId()));
        List<HousePictureDTO> housePictureDTOList = housePictures.stream().map(picture -> modelMapper.map(picture, HousePictureDTO.class)).collect(Collectors.toList());
        // 新增房屋标签信息
        houseTagRepository.saveAll(generateHouseTag(houseForm, house.getId()));
        // 设置返回结果
        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setCover(cdnPrefix + houseDTO.getCover());
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setHousePictureList(housePictureDTOList);
        houseDTO.setTags(houseForm.getTags());
        return houseDTO;
    }

    /**
     * 注入房屋详情信息
     */
    private HouseDetail generateHouseDetail(HouseForm houseForm){
        HouseDetail houseDetail = new HouseDetail();
        if(houseForm.getSubwayLineId() != null && houseForm.getSubwayStationId() != null){
            Subway subway = subwayRepository.findById(houseForm.getSubwayLineId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_LINE_ERROR));
            SubwayStation subwayStation = subwayStationRepository.findById(houseForm.getSubwayStationId()).orElseThrow(() -> new BusinessException(ApiResponseEnum.SUBWAY_STATION_ERROR));
            if(subway.getId() != subwayStation.getSubwayId()){
                throw new BusinessException(ApiResponseEnum.SUBWAY_AND_STATION_MATCH_ERROR);
            }
            houseDetail.setSubwayLineId(subway.getId());
            houseDetail.setSubwayLineName(subway.getName());
            houseDetail.setSubwayStationId(subwayStation.getId());
            houseDetail.setSubwayStationName(subwayStation.getName());
        }
        houseDetail.setAddress(houseForm.getAddress());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setTraffic(houseForm.getTraffic());
        houseDetail.setRoundService(houseForm.getRoundService());
        return houseDetail;
    }

    /**
     * 生成房屋照片列表
     */
    private List<HousePicture> generateHousePicture(HouseForm houseForm, Long houseId){
        List<HousePicture> housePictures = new ArrayList<>();
        if(CollectionUtils.isEmpty(houseForm.getPhotos())){
            return housePictures;
        }
        List<PhotoForm> photos = houseForm.getPhotos();
        housePictures = photos.stream().map(item -> {
            HousePicture housePicture = new HousePicture();
            housePicture.setCdnPrefix(cdnPrefix);
            housePicture.setHouseId(houseId);
            housePicture.setPath(item.getPath());
            housePicture.setHeight(item.getHeight());
            housePicture.setWidth(item.getWidth());
            return housePicture;
        }).collect(Collectors.toList());
        return housePictures;
    }

    /**
     * 生成房屋标签集合
     */
    private List<HouseTag> generateHouseTag(HouseForm houseForm, Long houseId){
        if(!CollectionUtils.isEmpty(houseForm.getTags())){
            return Collections.emptyList();
        }
        List<HouseTag> houseTagList = houseForm.getTags().stream().map(tag -> {
            HouseTag houseTag = new HouseTag();
            houseTag.setName(tag);
            houseTag.setHouseId(houseId);
            return houseTag;
        }).collect(Collectors.toList());
        return houseTagList;
    }
}
