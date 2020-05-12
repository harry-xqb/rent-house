package com.harry.renthouse.web.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 管理员房源搜索表单
 * @date 2020/5/11 17:55
 */
@Data
public class AdminHouseSearchForm {

    @Min(value = 1, message = "页号不能小于1")
    private int page = 1;

    @Min(value = 1, message = "页面大小不能小于1")
    private int size = 10;

    private Integer status;

    private LocalDate createTimeMin;

    private LocalDate createTimeMax;

    private String city;

    private String title;

    private String direction = "ASC";

    private String orderBy = "createTime";
}
