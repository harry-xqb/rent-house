package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/6/4 16:09
 */
public interface HouseSubscribeRepository extends JpaRepository<HouseSubscribe, Long> {

    Optional<HouseSubscribe> findByUserIdAndHouseId(Long userId, Long houseId);
}
