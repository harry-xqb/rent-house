package com.harry.renthouse.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harry Xu
 * @date 2020/6/1 11:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaiduMapLocation {

    private double lon;

    private double lat;
}
