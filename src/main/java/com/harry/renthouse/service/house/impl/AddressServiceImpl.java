package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.web.dto.SubwayDTO;
import com.harry.renthouse.web.dto.SubwayStationDTO;
import com.harry.renthouse.web.dto.SupportAddressDTO;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.repository.SubwayRepository;
import com.harry.renthouse.repository.SubwayStationRepository;
import com.harry.renthouse.repository.SupportAddressRepository;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.service.house.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Harry Xu
 * @date 2020/5/8 17:24
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    /**
     * 获取所有城市
     * @return
     */
    @Override
    public ServiceMultiResult findAllCities() {
        Optional<List<SupportAddress>> addressList = Optional.ofNullable(supportAddressRepository.findAllByLevel(SupportAddress.AddressLevel.CITY.getValue()));
        List<SupportAddressDTO> list = addressList.orElse(Collections.emptyList()).stream()
                .map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAreaByBelongToAndLevel(String belongTo, String level) {
        SupportAddress.AddressLevel levelEnum = SupportAddress.AddressLevel.of(level);
        List<SupportAddressDTO> list = Optional.ofNullable(supportAddressRepository.findAllByBelongToAndLevel(belongTo, levelEnum.getValue()))
                .orElse(Collections.emptyList())
                .stream().map(address -> modelMapper.map(address, SupportAddressDTO.class))
                .collect(Collectors.toList());
        ;
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAreaInEnName(List<String> enNameList) {
        List<SupportAddressDTO> list = Optional.ofNullable(supportAddressRepository.findAllByEnNameIn(enNameList))
                .orElse(Collections.emptyList())
                .stream().map(item -> modelMapper.map(item, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return new ServiceMultiResult<>(list.size(), list);
    }

    @Override
    public ServiceMultiResult<SubwayDTO> findAllSubwayByCityEnName(String cityEnName) {
        List<SubwayDTO> subWayDtoList = Optional.ofNullable(subwayRepository.findAllByCityEnName(cityEnName))
                .orElse(Collections.emptyList()).stream().map(subway -> modelMapper.map(subway, SubwayDTO.class)).collect(Collectors.toList());
        return new ServiceMultiResult<>(subWayDtoList.size(), subWayDtoList);
    }

    @Override
    public ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId) {
        List<SubwayStationDTO> subwayStationDTOList = Optional.ofNullable(subwayStationRepository
                .getAllBySubwayId(subwayId)).orElse(Collections.emptyList()).stream()
                .map(subwayStation -> modelMapper.map(subwayStation, SubwayStationDTO.class)).collect(Collectors.toList());
        return new ServiceMultiResult<>(subwayStationDTOList.size(), subwayStationDTOList);
    }

}
