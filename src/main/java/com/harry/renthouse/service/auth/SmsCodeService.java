package com.harry.renthouse.service.auth;

import com.harry.renthouse.web.form.SendSmsForm;

/**
 * @author Harry Xu
 * @date 2020/5/22 11:48
 */
public interface SmsCodeService {

    void sendSms(SendSmsForm sendSmsForm);

    void validate(String phone, String code, String operationType);
}
