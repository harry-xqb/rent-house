package com.harry.renthouse.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harry Xu
 * @date 2020/5/21 11:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseKafkaMessage {

    public static final String INDEX = "index";

    public static final String DELETE = "delete";

    public static final int MAX_RETRY = 3;

    private Long id;

    private String operation;

    private int retry;
}
