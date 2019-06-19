package com.qpg.aop.trace;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface DeleteSpTrace {

    String key();

    boolean allEntries() default false;//是否清空所有
}
