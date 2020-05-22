package com.harry.renthouse.web.form.annotation.validate;

import com.harry.renthouse.web.form.annotation.PasswordStrongAnnotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Harry Xu
 * @date 2020/5/22 10:22
 */
public class PasswordStrongValidate implements ConstraintValidator<PasswordStrongAnnotation, String> {

    // 数字和字母组合8-16位
    private static String regex = "^(?=.*\\d)((?=.*[a-z])|(?=.*[A-Z])).{8,16}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches(regex);
    }
}
