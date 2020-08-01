package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseSubscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Harry Xu
 * @date 2020/6/4 16:09
 */
public interface HouseSubscribeRepository extends JpaRepository<HouseSubscribe, Long> {

    Optional<HouseSubscribe> findByUserIdAndHouseId(Long userId, Long houseId);

    boolean existsByHouseIdAndUserId(Long houseId, Long userId);

    Page<HouseSubscribe> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    Page<HouseSubscribe> findByAdminIdAndStatus(Long adminId, Integer status, Pageable pageable);

}
