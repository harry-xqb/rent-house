package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HousePicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  房屋图片dao
 * @author Harry Xu
 * @date 2020/5/9 14:30
 */
public interface HousePictureRepository extends JpaRepository<HousePicture, Long> {

    List<HousePicture> findAllByHouseId(Long houseId);

    void deleteAllByHouseId(Long houseId);
}
