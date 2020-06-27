package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseDetail;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 *  房屋详情dao
 * @author Harry Xu
 * @date 2020/5/9 14:26
 */
public interface HouseDetailRepository extends JpaRepository<HouseDetail, Long> {

    Optional<HouseDetail> findByHouseId(Long id);

    List<HouseDetail> findAllByHouseIdIn(List<Long> houseIdList);

    List<HouseDetail> findAllByRentWay(int rentWay);

    // 查找所有指定subwayLineId的房源id
    @Query("select houseId from HouseDetail where subwayLineId = :subwayLineId group by houseId")
    List<Long> findAllHouseIdBySubwayLineId(Long subwayLineId);

    // 查找所有指定地铁线路并且存在地铁站集合中的房源id
    @Query("select houseId from HouseDetail where subwayLineId = :subwayLineId and subwayStationId in (:subwayStationIdList) group by houseId")
    List<Long> findAllHouseIdBySubwayLineIdAndSubwayStationIdIn(Long subwayLineId, List<Long> subwayStationIdList);
}
