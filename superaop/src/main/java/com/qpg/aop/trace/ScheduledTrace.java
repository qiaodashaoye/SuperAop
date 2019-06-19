package com.qpg.aop.trace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScheduledTrace {

    /**
     * 初始化延迟
     *
     * @return
     */
    long initialDelay() default 0L;

    /**
     * 时间间隔
     *
     * @return
     */
    long interval();

    /**
     * 时间单位
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 执行次数
     *
     * @return
     */
    int count() default Integer.MAX_VALUE;

    /**
     * 定时任务到期回调
     *
     * @return
     */
    String taskExpiredCallback() default "";
}
