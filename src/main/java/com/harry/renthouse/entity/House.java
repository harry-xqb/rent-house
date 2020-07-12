package com.harry.renthouse.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 房屋实体类
 * @author Harry Xu
 * @date 2020/5/9 14:14
 */
@Entity
@Data
@DynamicUpdate
@DynamicInsert
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer price;

    private Integer area;

    private Integer room;

    private Integer floor;

    private Integer totalFloor;

    private Integer watchTimes;

    private Integer buildYear;

    private Integer status;

    private Date createTime;

    private Date lastUpdateTime;

    private String cityEnName;

    private String regionEnName;

    private String cover;

    /* 房屋朝向: 1:东 2:南 3:西  4:北 */
    private Integer direction;

    /* 到地铁的距离 */
    private Integer distanceToSubway;

    /** 客厅数量 **/
    private Integer parlour;

    /* 小区名称 */
    private String district;

    /* 所属管理员id */
    private Long adminId;

    private Integer bathroom;

    /* 街道 */
    private String street;

}
