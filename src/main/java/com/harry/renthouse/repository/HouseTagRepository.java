package com.harry.renthouse.repository;

import com.harry.renthouse.entity.HouseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * 查询匹配所有标签的房源id
     * @param tags 标签集合
     * @param tagsLength 集合大小
     */
    @Query(value = "SELECT houseId FROM HouseTag WHERE name IN (:tags) GROUP BY houseId HAVING count(houseId) = :tagsLength")
    List<Long> findALLHouseIdMatchTags(List<String> tags, long tagsLength);

}
