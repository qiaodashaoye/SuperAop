package com.qpg.aop.trace;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface RetryTrace {
    /**
     * 重试次数
     * @return
     */
    int count() default 0;


    /**
     * 重试的间隔时间
     * @return
     */
    long delay() default 0L;


    /**
     * 是否支持异步重试方式
     * @return
     */
    boolean asyn() default false;

    /**
     * 重试n次后，结果的回调
     * @return
     */
    String retryCallback() default "";
}
