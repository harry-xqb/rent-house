package com.harry.renthouse.web.form.annotation.validate;

import com.harry.renthouse.property.LimitsProperty;
import com.harry.renthouse.web.form.annotation.PhoneAnnotation;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Harry Xu
 * @date 2020/6/3 16:49
 */
public class PhoneValidate implements ConstraintValidator<PhoneAnnotation, String> {
    @Resource
    private LimitsProperty limitsProperty;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 手机号正则校验
        String regex = limitsProperty.getPhoneRegex();
        return value.matches(regex);
    }
}
