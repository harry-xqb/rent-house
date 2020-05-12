package com.harry.renthouse.web.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 *  房屋照片对象
 * @author Harry Xu
 * @date 2020/5/11 15:47
 */
@Data
public class PictureForm {

    private Long id;

    private Integer width;

    private Integer height;

    @NotNull(message = "照片路径不能为空")
    private String path;
}
