package com.harry.renthouse.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry Xu
 * @date 2020/6/8 13:58
 */
@Configuration
@ConfigurationProperties(prefix = "esmonitor")
@EnableConfigurationProperties
@Data
public class EsMonitorProperty {

    private String mailForm;

    private String mailTo;

    private String mailTitle;

    private String api;

}
