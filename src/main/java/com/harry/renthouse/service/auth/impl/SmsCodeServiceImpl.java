package com.harry.renthouse.service.auth.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.harry.renthouse.base.ApiResponseEnum;
import com.harry.renthouse.exception.BusinessException;
import com.harry.renthouse.property.ALiYunSmsProperty;
import com.harry.renthouse.service.auth.SmsCodeService;
import com.harry.renthouse.validate.code.SmsCodeGenerator;
import com.harry.renthouse.validate.code.ValidateCodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Harry Xu
 * @date 2020/5/22 11:54
 */
@Service
@Slf4j
public class SmsCodeServiceImpl implements SmsCodeService {

    @Resource
    private ALiYunSmsProperty aLiYunSmsProperties;

    @Resource
    private SmsCodeGenerator smsCodeGenerator;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Gson gson;

    private static final int REDIS_SMS_CONTENT_KEY_EXPIRE = 60 * 15; // 内容key15分钟过期

    private static final int REDIS_SMS_INTERVAL_KEY_EXPIRE = 60; // 间隔key 1分钟过期

    private static final String SMS_CODE_INTERVAL_PREFIX = "SMS:CODE:INTERVAL:";
    private static final String SMS_CODE_CONTENT_PREFIX = "SMS:CODE:CONTENT:";

    @Override
    public String sendSms(String phone, String operationType) {
        String intervalKey = SMS_CODE_INTERVAL_PREFIX + generateSmsKey(phone, operationType);
        if(stringRedisTemplate.opsForValue().get(intervalKey) != null){
            throw new BusinessException(ApiResponseEnum.PHONE_SEND_SMS_BUSY);
        }
        String contentKey = SMS_CODE_CONTENT_PREFIX + generateSmsKey(phone, operationType);
        String code = RandomStringUtils.randomNumeric(aLiYunSmsProperties.getLength());
        aliSendSms(phone, code);
        log.debug("验证码:{}", code);
        stringRedisTemplate.opsForValue().set(contentKey, code, REDIS_SMS_CONTENT_KEY_EXPIRE, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(intervalKey, code, REDIS_SMS_INTERVAL_KEY_EXPIRE, TimeUnit.SECONDS);
        return code;
    }
    @Override
    public String getSmsCode(String phone, String operationType) {
        String contentKey = SMS_CODE_CONTENT_PREFIX + generateSmsKey(phone, operationType);
        return stringRedisTemplate.opsForValue().get(contentKey);
    }

    public void deleteSmsCode(String phone, String operationType){
        stringRedisTemplate.delete(SMS_CODE_CONTENT_PREFIX + generateSmsKey(phone, operationType));
    }

    private String generateSmsKey(String phone, String type){
        ValidateCodeTypeEnum validateCodeTypeEnum = ValidateCodeTypeEnum.fromValue(type).orElseThrow(() -> new BusinessException(ApiResponseEnum.PHONE_SMS_NOT_VALID_TYPE));
        return validateCodeTypeEnum.getValue() + "::" + phone;
    }

    public void aliSendSms(String phone, String code){
        try{
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", aLiYunSmsProperties.getAccessKey(), aLiYunSmsProperties.getAccessSecret());
            IAcsClient client = new DefaultAcsClient(profile);
            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(aLiYunSmsProperties.getSignName());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(aLiYunSmsProperties.getTemplateCode());
            request.setTemplateParam("{\"code\":\"" + code + "\"}");
            SendSmsResponse acsResponse = client.getAcsResponse(request);
            if(!StringUtils.equalsIgnoreCase(acsResponse.getCode(), "OK")){
                log.warn("发送短信失败: {}", acsResponse.getMessage());
                throw new BusinessException(ApiResponseEnum.PHONE_SEND_SMS_ERROR);
            }
        }catch (ClientException e){
            log.error("短信客户端异常:", e);
            throw new BusinessException(ApiResponseEnum.PHONE_SEND_SMS_ERROR);
        }
    }
}
