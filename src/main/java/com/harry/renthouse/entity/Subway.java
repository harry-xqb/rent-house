package com.harry.renthouse.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 地铁线路实体类
 * @author Harry Xu
 * @date 2020/5/9 10:02
 */
@Entity
@Data
public class Subway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String cityEnName;
}
