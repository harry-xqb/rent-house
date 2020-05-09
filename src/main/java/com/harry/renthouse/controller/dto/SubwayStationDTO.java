package com.harry.renthouse.controller.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Harry Xu
 * @date 2020/5/9 13:55
 */
@Data
public class SubwayStationDTO {

    private Long id;

    private Long subwayId;

    private String name;

}
