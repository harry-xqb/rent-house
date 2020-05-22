package com.harry.renthouse.service.auth.impl;

import com.harry.renthouse.RentHouseApplicationTests;
import com.harry.renthouse.service.auth.SmsCodeService;
import com.harry.renthouse.validate.code.ValidateCodeTypeEnum;
import com.harry.renthouse.web.form.SendSmsForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Harry Xu
 * @date 2020/5/22 15:59
 */
class SmsCodeServiceImplTest extends RentHouseApplicationTests {

    @Resource
    private SmsCodeServiceImpl smsCodeService;

    @Test
    void aliSendSms() {
        smsCodeService.aliSendSms("17879502601", "520");
    }

    @Test
    void sendSms(){
        SendSmsForm sendSmsForm = new SendSmsForm();
        sendSmsForm.setOperationType(ValidateCodeTypeEnum.LOGIN.getValue());
        sendSmsForm.setPhoneNumber("17879502601");
        smsCodeService.sendSms(sendSmsForm);
    }
}