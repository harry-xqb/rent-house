package com.harry.renthouse.config;

import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry Xu
 * @date 2020/5/9 17:33
 */
@Configuration
public class WebUploadFileConfig {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    /**
     * 创建七牛云认证对象
     */
    @Bean
    public Auth auth(){
        return Auth.create(accessKey, secretKey);
    }

    /**
     * 创建七牛云服务器配置
     */
    @Bean
    public com.qiniu.storage.Configuration qiniuConfiguration(){
        return new com.qiniu.storage.Configuration(Region.region2());
    }

    /**
     * 七牛云上传对象
     * @return
     */
    @Bean
    public UploadManager uploadManager(){
        return new UploadManager(qiniuConfiguration());
    }
}
