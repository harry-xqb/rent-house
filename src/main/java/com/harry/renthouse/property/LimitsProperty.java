package com.harry.renthouse.property;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry Xu
 * @date 2020/5/27 11:19
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "open.limits")
public class LimitsProperty{

    private String userPasswordRegex = "^(?=.*\\d)((?=.*[a-z])|(?=.*[A-Z])).{8,16}$";

    public String phoneRegex = "^(1[3-9]\\d{9}$)";

    public long avatarSizeLimit = 5242880;

    public String[] avatarTypeLimit = {"jpg", "png", "jpeg"};

    public long housePhotoSizeLimit = 10485760;

    public String[] housePhotoTypeLimit = {"jpg", "png", "jpeg", "webp"};
}
