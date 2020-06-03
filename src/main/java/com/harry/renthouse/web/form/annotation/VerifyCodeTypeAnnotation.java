package com.harry.renthouse.web.form.annotation;

import com.harry.renthouse.web.form.annotation.validate.PasswordStrongValidate;
import com.harry.renthouse.web.form.annotation.validate.VerifyCodeTypeValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 *  验证码业务类型
 * @author Harry Xu
 * @date 2020/5/22 11:37
 */
@Target(ElementType.FIELD)//目标是字段
@Retention(RetentionPolicy.RUNTIME) //注解会在class中存在，运行时可通过反射获取
@Inherited
@Constraint(validatedBy = VerifyCodeTypeValidate.class)
public @interface VerifyCodeTypeAnnotation {

    String message() default "无效的验证码类型";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
}