package com.chat.crazy.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hncboy
 * @date 2023/3/23 00:18
 * 前端鉴权注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrontPreAuth {
    int customize() default 0; // 0: 通用auth校验， 1：自定义校验

    boolean tokenAuth() default true;

    boolean virIdAuth() default true;
}
