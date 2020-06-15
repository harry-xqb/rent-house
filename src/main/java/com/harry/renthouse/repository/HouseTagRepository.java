package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *  房屋标签dao
 * @author Harry Xu
 * @date 2020/5/9 14:14
 */
public interface HouseTagRepository  extends JpaRepository<HouseTag, Long> {

    List<HouseTag> findAllByHouseId(Long houseId);

    Optional<HouseTag> findByNameAndHouseId(String name, Long houseId);

    List<HouseTag> findAllByHouseIdIn(List<Long> houseIdList);

    List<HouseTag> findAllByNameIn(List<String> nameList);

    void deleteAllByHouseId(Long houseId);
}
