package com.harry.renthouse.service.auth;

import com.harry.renthouse.web.form.SendSmsForm;

/**
 * @author Harry Xu
 * @date 2020/5/22 11:48
 */
public interface SmsCodeService {

    /**
     * 发送短信
     * @param phone 手机号
     * @param operationType 业务类型
     */
    String sendSms(String phone, String operationType);

    /**
     * 获得短信
     * @param phone 手机号
     * @param operationType 业务类型
     */
    String getSmsCode(String phone, String operationType);

    /**
     * 删除短信
     * @param phone 手机号
     * @param operationType 业务类型
     */
    void deleteSmsCode(String phone, String operationType);
}
