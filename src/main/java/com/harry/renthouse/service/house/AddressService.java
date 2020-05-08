package com.harry.renthouse.service.house;

import com.harry.renthouse.controller.house.SupportAddressDTO;
import com.harry.renthouse.entity.SupportAddress;
import com.harry.renthouse.service.ServiceMultiResult;

import java.util.List;

/**
 * @author Harry Xu
 * @date 2020/5/8 17:10
 */
public interface AddressService {

    ServiceMultiResult<SupportAddressDTO> findAllCities();
}
