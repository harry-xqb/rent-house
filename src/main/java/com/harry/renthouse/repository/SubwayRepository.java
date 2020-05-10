package com.harry.renthouse.repository;

import com.harry.renthouse.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  地铁线路dao
 * @author Harry Xu
 * @date 2020/5/9 10:05
 */
public interface SubwayRepository extends JpaRepository<Subway, Long> {

    /**
     * 通过城市拼音缩写查找所有地铁线路
     * @param cityEnName 城市拼音缩写
     * @return 地铁线路列表
     */
    List<Subway> findAllByCityEnName(String cityEnName);
}
