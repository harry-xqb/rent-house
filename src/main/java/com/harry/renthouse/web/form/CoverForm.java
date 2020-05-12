package com.harry.renthouse.web.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Harry Xu
 * @date 2020/5/12 18:20
 */
@Data
public class CoverForm {

    @NotNull(message = "封面id不能为空")
    private Long coverId;

    @NotNull(message = "房屋id不能为空")
    private Long houseId;
}
