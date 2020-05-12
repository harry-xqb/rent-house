package com.harry.renthouse.web.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/5/12 17:19
 */
@Data
public class TagForm {

    @NotNull(message = "标签名称不能为空")
    private String name;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;
}
