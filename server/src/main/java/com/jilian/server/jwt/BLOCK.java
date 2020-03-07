package com.jilian.server.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 7:53 下午 2020/2/4
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BLOCK {
    boolean required() default true;
}
