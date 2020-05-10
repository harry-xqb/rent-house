package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  房屋详情dao
 * @author Harry Xu
 * @date 2020/5/9 14:26
 */
public interface HouseDetailRepository extends JpaRepository<HouseDetail, Long> {

}
