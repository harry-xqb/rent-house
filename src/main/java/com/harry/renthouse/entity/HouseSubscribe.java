package com.harry.renthouse.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *  看房预约实体类
 * @author Harry Xu
 * @date 2020/5/9 14:33
 */
@Entity
@Data
public class HouseSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long houseId;

    private Long userId;

    /* 用户描述 */
    private String desc;

    /* 预约状态: 1: 加入待看清单  2: 已约看房时间  3: 看房完成*/
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime lastUpdateTime;

    /* 预约时间 */
    private LocalDateTime orderTime;

    private  String telephone;

    /* 房源发布者id */
    private Long adminId;
}
