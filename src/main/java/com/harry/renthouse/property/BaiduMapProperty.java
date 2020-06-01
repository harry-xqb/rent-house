package com.harry.renthouse.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry Xu
 * @date 2020/6/1 11:42
 */
@Configuration
@ConfigurationProperties(prefix = "baidu.map")
@EnableConfigurationProperties
@Data
public class BaiduMapProperty {

    private String accessKey;

    private String url;
}
