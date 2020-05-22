package com.harry.renthouse.validate.code;

import com.harry.renthouse.property.ALiYunSmsProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Harry Xu
 * @date 2020/5/22 13:43
 */
@Component
public class SmsCodeGenerator {

    @Autowired
    private ALiYunSmsProperties aLiYunSmsProperties;

    public ValidateCode generate() {
        String code = RandomStringUtils.randomNumeric(aLiYunSmsProperties.getLength());
        int expireIn = aLiYunSmsProperties.getExpireIn();
        int resendInterval = aLiYunSmsProperties.getResendInterval();
        return new ValidateCode(code, expireIn, resendInterval);
    }
}
