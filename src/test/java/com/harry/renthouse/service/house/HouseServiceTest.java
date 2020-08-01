package com.harry.renthouse.service.house;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.service.ServiceMultiResult;
import com.harry.renthouse.web.dto.HouseStarDTO;
import com.harry.renthouse.web.form.ListHouseStarForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/7/28 10:39
 */
class HouseServiceTest  extends RentHouseApplicationTests {

    @Autowired
    private HouseService houseService;

    @Test
    @WithUserDetails(value = "harry")
    void starHouse() {
        houseService.starHouse(57L);
    }

    @Test
    @WithUserDetails(value = "harry")
    void userStarHouseList() {
        ListHouseStarForm listHouseStarForm = new ListHouseStarForm();
        ServiceMultiResult<HouseStarDTO> result = houseService.userStarHouseList(listHouseStarForm);
        Assert.isTrue(result.getTotal() == 2, "总数不匹配");
    }

    @Test
    @WithUserDetails(value = "harry")
    void deleteStarInfo() {
        houseService.deleteStarInfo(58L);
    }

    @Test
    @WithUserDetails(value = "harry")
    void isStar() {
        /*boolean star = houseService.isStar(58L);
        Assert.isTrue(star, "收藏状态不匹配");*/
    }
}