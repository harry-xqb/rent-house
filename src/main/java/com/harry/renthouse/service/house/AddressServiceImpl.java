package com.harry.renthouse.service.house;

import com.harry.renthouse.controller.house.SupportAddressDTO;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.repository.SupportAddressRepository;
import com.harry.renthouse.service.ServiceMultiResult;
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
}
