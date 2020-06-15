package com.harry.renthouse.service.house.impl;

import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponse;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.service.house.QiniuService;
import com.harry.renthouse.web.dto.QiniuUploadResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *  七牛云service实现
 * @author Harry Xu
 * @date 2020/5/9 17:41
 */
@Service
public class QiniuServiceImpl implements QiniuService, InitializingBean {

    @Resource
    private Auth auth;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private BucketManager bucketManager;

    @Resource
    private Gson gson;

    @Value("${qiniu.bucket}")
    private String bucket;

    private StringMap uploadPolicy;

    @Value("${qiniu.cdnPrefix}")
    private String cndPrefix;


    @Override
    public Response uploadFile(File file) throws QiniuException {
        String token = getUploadToken();
        Response response = uploadManager.put(file, null, token);
        int retryCount = 0;
        while(response.needRetry() && retryCount++ < 3){
            response = uploadManager.put(file, null, token);
        }
        return response;
    }

    @Override
    public QiniuUploadResult uploadFile(InputStream inputStream) throws QiniuException {
        String token = getUploadToken();
        Response response = uploadManager.put(inputStream, null, token, null, null);
        int retryCount = 0;
        while(response.needRetry() && retryCount++ < 3){
            response = uploadManager.put(inputStream, null, token, null, null);
        }
        try {
            QiniuUploadResult qiniuUploadResult = gson.fromJson(response.bodyString(), QiniuUploadResult.class);
            qiniuUploadResult.setCdnPrefix(cndPrefix);
            return qiniuUploadResult;
        }catch (QiniuException e){
            response = e.response;
            try {
                throw new BusinessException(response.bodyString(), response.statusCode);
            } catch (QiniuException e1) {
                e1.printStackTrace();
                throw new BusinessException(ApiResponseEnum.FILE_UPLOAD_ERROR);
            }
        }
    }

    @Override
    public Response deleteFile(String key) throws QiniuException {
        Response response = bucketManager.delete(bucket, key);
        int retryCount = 0;
        while(response.needRetry() && retryCount++ < 3){
            response = bucketManager.delete(bucket, key);
        }
        return response;
    }

    private String getUploadToken(){
        return auth.uploadToken(bucket, null, 3600, uploadPolicy);
    }

    @Override
    public void afterPropertiesSet(){
        uploadPolicy = new StringMap();
        uploadPolicy.put("returnBody",
                "{\"key\":\"$(key)\", " +
                "\"hash\":\"$(etag)\"," +
                "\"bucket\":\"$(bucket)\"," +
                "\"height\":$(imageInfo.height)," +
                "\"width\":$(imageInfo.width)}");
    }
}
