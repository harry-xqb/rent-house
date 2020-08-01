package com.harry.renthouse.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Harry Xu
 * @date 2020/7/27 16:41
 */
@Entity
@Data
@DynamicUpdate
@DynamicInsert
public class HouseStar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long houseId;

    private Long userId;

    private Date createTime;

    private Date lastUpdateTime;

}
