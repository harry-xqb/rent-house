package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseTag;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  房屋标签dao
 * @author Harry Xu
 * @date 2020/5/9 14:14
 */
public interface HouseTagRepository  extends JpaRepository<HouseTag, Long> {
}
