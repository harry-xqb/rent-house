package com.harry.renthouse.web.form;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 管理员房源搜索表单
 * @author Harry Xu
 * @date 2020/5/11 17:55
 */
@Data
public class AdminHouseSearchForm {

    private Integer start;

    private Integer length;

    private Integer status;

    private Date createTimeMin;

    private Date createTimeMax;

    private String city;

    private String title;

    private String direction;
}
