package com.harry.renthouse.repository;

import com.harry.renthouse.entity.SubwayStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  地铁站dao
 * @author Harry Xu
 * @date 2020/5/9 10:06
 */
public interface SubwayStationRepository  extends JpaRepository<SubwayStation, Long> {

    /**
     * 通过地铁线路id查找地铁站
     * @param subwayId 地铁线路id
     * @return 地铁站列表
     */
    List<SubwayStation> getAllBySubwayId(Long subwayId);
}
