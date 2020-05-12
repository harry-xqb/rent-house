package com.harry.renthouse.repository;

import com.harry.renthouse.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *  房屋dao
 * @author Harry Xu
 * @date 2020/5/9 14:21
 */
public interface HouseRepository  extends JpaRepository<House, Long>, JpaSpecificationExecutor<House> {

}
