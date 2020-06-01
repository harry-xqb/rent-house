package com.harry.renthouse.elastic.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Harry Xu
 * @date 2020/6/1 11:16
 */
@Data
public class BaiduMapLocation {

    private double lon;

    private double lat;
}
