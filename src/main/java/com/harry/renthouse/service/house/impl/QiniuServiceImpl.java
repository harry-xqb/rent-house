package com.harry.renthouse.service.house.impl;

import com.harry.renthouse.service.house.QiniuService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 *  七牛云service实现
 * @author Harry Xu
 * @date 2020/5/9 17:41
 */
@Service
public class QiniuServiceImpl implements QiniuService, InitializingBean {

    @Autowired
    private Auth auth;

    @Autowired
    private UploadManager uploadManager;

    @Value("${qiniu.bucket}")
    private String bucket;

    private StringMap uploadPolicy;


    @Override
    public Response uploadFile(File file) throws QiniuException {
        String token = getUploadToken();
        System.out.println("token:" + token);
        Response response = uploadManager.put(file, null, token);
        int retryCount = 0;
        while(response.needRetry() && retryCount++ < 3){
            response = uploadManager.put(file, null, token);
        }
        return response;
    }

    @Override
    public Response uploadFile(InputStream inputStream) throws QiniuException {
        return null;
    }

    @Override
    public Response deleteFile(String key) throws QiniuException {
        return null;
    }

    private String getUploadToken(){
        return auth.uploadToken(bucket, null, 3600, uploadPolicy);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        uploadPolicy = new StringMap();
        uploadPolicy.put("returnBody",
                "{\"key\":\"$(key)\", " +
                "\"hash\":\"$(etag)\"," +
                "\"bucket\":\"$(bucket)\"," +
                "\"height\":$(height)," +
                "\"width\":$(width)}");
    }
}
