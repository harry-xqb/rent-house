package com.harry.renthouse.repository;

import com.harry.renthouse.entity.SupportAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *  支持的地区dao
 * @author Harry Xu
 * @date 2020/5/8 17:04
 */
public interface SupportAddressRepository  extends JpaRepository<SupportAddress, Long> {

    /**
     * 查询所有对应行政级别的信息
     * @param level 行政级别
     * @return 行政单位列表
     */
    List<SupportAddress> findAllByLevel(String level);

    /**
     * 通过所属行政单位简称和行政单位级别查询区域列表
     * @param belongTo 所属行政单位
     * @param level 行政级别
     * @return 行政单位列表
     */
    List<SupportAddress> findAllByBelongToAndLevel(String belongTo, String level);

    /**
     * 查找所有给定的enName地址
     * @param enNameList 城市/区域 enName 列表
     * @return 行政单位列表
     */
    List<SupportAddress> findAllByEnNameIn(List<String> enNameList);

    /**
     * 通过英文简称和等级查找地区
     * @param enName 英文简称
     * @param level 等级
     */
    Optional<SupportAddress> findByEnNameAndLevel(String enName, String level);

    /**
     * 通过所属简称和自身简称查找
     * @param belongTo 所属上级单位
     * @param enName 自身简称
     */
    Optional<SupportAddress> findByBelongToAndEnNameAndLevel(String belongTo, String enName, String level);

}
