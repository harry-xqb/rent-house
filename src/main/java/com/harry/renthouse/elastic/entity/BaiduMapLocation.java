package com.harry.renthouse.elastic.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Harry Xu
 * @date 2020/6/1 11:16
 */
@Data
public class BaiduMapLocation {

    @JsonProperty("lon")
    private double longitude;

    @JsonProperty("lat")
    private double latitude;
}
