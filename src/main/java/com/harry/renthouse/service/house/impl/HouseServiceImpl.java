package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.base.AuthenticatedUserUtil;
import com.harry.renthouse.controller.dto.HouseDTO;
import com.harry.renthouse.controller.form.HouseForm;
import com.harry.renthouse.entity.House;
import com.harry.renthouse.repository.HouseRepository;
import com.harry.renthouse.service.ServiceResult;
import com.harry.renthouse.service.house.HouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public HouseDTO addHouse(HouseForm houseForm) {
        House house = modelMapper.map(houseForm, House.class);
        house.setCreateTime(LocalDateTime.now());
        house.setLastUpdateTime(LocalDateTime.now());
        house.setAdminId(AuthenticatedUserUtil.getUserId());
        House savedHouse = houseRepository.save(house);
        HouseDTO houseDTO = modelMapper.map(savedHouse, HouseDTO.class);
        return houseDTO;
    }
}
