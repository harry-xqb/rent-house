package com.harry.renthouse.web.form.annotation;

import com.harry.renthouse.web.form.annotation.validate.PasswordStrongValidate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 密码强度校验注解
 * @author Harry Xu
 * @date 2020/5/22 10:15
 */
@Target(ElementType.FIELD)//目标是字段
@Retention(RetentionPolicy.RUNTIME) //注解会在class中存在，运行时可通过反射获取
@Inherited
@Constraint(validatedBy = PasswordStrongValidate.class)
public @interface PasswordStrongAnnotation {

    String message() default "密码需为8-16位数字与字母组合";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
}
