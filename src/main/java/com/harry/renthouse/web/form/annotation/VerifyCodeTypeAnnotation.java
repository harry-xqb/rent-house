package com.harry.renthouse.web.form.annotation;

import java.lang.annotation.*;

/**
 *  验证码业务类型
 * @author Harry Xu
 * @date 2020/5/22 11:37
 */
@Documented //文档生成时，该注解将被包含在javadoc中，可去掉
@Target(ElementType.FIELD)//目标是字段
@Retention(RetentionPolicy.RUNTIME) //注解会在class中存在，运行时可通过反射获取
@Inherited
public @interface VerifyCodeTypeAnnotation {

    String message() default "无效的验证码类型";
}