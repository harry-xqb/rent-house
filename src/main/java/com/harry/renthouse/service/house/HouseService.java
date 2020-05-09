package com.harry.renthouse.service.house;

import com.harry.renthouse.controller.dto.HouseDTO;
import com.harry.renthouse.controller.form.HouseForm;
import com.harry.renthouse.service.ServiceResult;

/**
 * 房屋service
 * @author Harry Xu
 * @date 2020/5/9 15:05
 */
public interface HouseService {


    /**
     * 新增房源
     * @param houseForm 房源表单
     * @return 房源信息
     */
    HouseDTO addHouse(HouseForm houseForm);
}
