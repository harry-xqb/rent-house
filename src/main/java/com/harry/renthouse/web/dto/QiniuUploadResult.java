package com.harry.renthouse.web.dto;

import lombok.Data;
import lombok.ToString;

/**
 *  七牛云上传结果
 * create by： harry
 * date:  2020/5/10 0010 下午 8:02
 **/
@Data
@ToString
public class QiniuUploadResult {

    private String key;

    private String hash;

    private String bucket;

    private Integer width;

    private Integer height;

}
