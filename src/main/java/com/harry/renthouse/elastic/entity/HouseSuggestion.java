package com.harry.renthouse.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索补全
 * @author Harry Xu
 * @date 2020/5/25 15:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HouseSuggestion {

    private String input;

    private Integer weight = 10;

    public HouseSuggestion(String input) {
        this.input = input;
    }
}
