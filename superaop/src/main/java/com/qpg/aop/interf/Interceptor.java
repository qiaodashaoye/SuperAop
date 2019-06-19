package com.qpg.aop.interf;


public interface Interceptor {
    boolean intercept(String key, String methodName) throws Throwable;
}
