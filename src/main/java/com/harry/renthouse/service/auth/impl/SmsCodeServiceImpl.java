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
import com.harry.renthouse.validate.code.ValidateCode;
import com.harry.renthouse.validate.code.ValidateCodeTypeEnum;
import com.harry.renthouse.web.form.SendSmsForm;
import lombok.extern.slf4j.Slf4j;
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

    public static final int REDIS_SMS_KEY_EXPIRE_PLUS = 60 * 15; // key在验证码过期后的15分钟后过期

    @Override
    public void sendSms(SendSmsForm sendSmsForm){
        String smsKey = generateSmsKey(sendSmsForm.getPhoneNumber(), sendSmsForm.getOperationType());
        String smsCode = stringRedisTemplate.opsForValue().get(smsKey);
        if(StringUtils.isNotBlank(smsCode)){
            ValidateCode validateCode = gson.fromJson(smsCode, ValidateCode.class);
            if(!validateCode.canReSend()){
                throw new BusinessException(ApiResponseEnum.PHONE_SEND_SMS_BUSY);
            }
        }
        ValidateCode validateCode = smsCodeGenerator.generate();
        aliSendSms(sendSmsForm.getPhoneNumber(), validateCode.getCode());
        stringRedisTemplate.opsForValue().set(smsKey, gson.toJson(validateCode),
                aLiYunSmsProperties.getExpireIn() + REDIS_SMS_KEY_EXPIRE_PLUS, TimeUnit.SECONDS);
    }

    @Override
    public void validate(String phone, String code, String operationType) {
        String smsKey = generateSmsKey(phone, operationType);
        String redisCodeStr = stringRedisTemplate.opsForValue().get(smsKey);
        if(StringUtils.isBlank(redisCodeStr)){
            throw new BusinessException(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
        }
        ValidateCode validateCode = gson.fromJson(redisCodeStr, ValidateCode.class);
        if(!StringUtils.equals(validateCode.getCode(), code)){
            throw new BusinessException(ApiResponseEnum.PHONE_SMS_CODE_ERROR);
        }
        if(validateCode.isExpired()){
            throw new BusinessException(ApiResponseEnum.PHONE_SMS_CODE_EXPIRE);
        }
        stringRedisTemplate.delete(smsKey);
    }

    private String generateSmsKey(String phone, String type){
        ValidateCodeTypeEnum validateCodeTypeEnum = ValidateCodeTypeEnum.fromValue(type).orElseThrow(() -> new BusinessException(ApiResponseEnum.PHONE_SMS_NOT_VALID_TYPE));
        return phone + ":" + validateCodeTypeEnum.getValue();
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
