package com.harry.renthouse.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *  服务端统一列表返回格式
 * @author Harry Xu
 * @date 2020/5/8 17:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceMultiResult<T> implements Serializable {

    @ApiModelProperty(value = "总数")
    private Integer total;

    @ApiModelProperty(value = "结果集")
    private List<T> list;

    @JsonIgnore
    public int resultSize(){
        return Optional.ofNullable(list).orElse(Collections.emptyList()).size();
    }
}
