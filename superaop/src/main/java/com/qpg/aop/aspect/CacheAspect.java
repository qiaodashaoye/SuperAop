package com.qpg.aop.aspect;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.CacheTrace;
import com.safframework.cache.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.Serializable;
import java.lang.reflect.Method;

@Aspect
public class CacheAspect {

    @Around("execution(!synthetic * *(..)) && onCacheMethod()")
    public Object doCacheMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return cacheMethod(joinPoint);
    }

    @Pointcut("@within(com.qpg.aop.trace.CacheTrace)||@annotation(com.qpg.aop.trace.CacheTrace)")
    public void onCacheMethod() {
    }

    private Object cacheMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        CacheTrace cacheable = method.getAnnotation(CacheTrace.class);
        Object result = null;

        if (cacheable!=null) {
            String key = cacheable.key();
            int expiry = cacheable.expiry();

            result = joinPoint.proceed();
            Cache cache = Cache.get(SuperAop.getInstance().getContext());
            if (expiry>0) {
                cache.put(key,(Serializable)result,expiry);
            } else {
                cache.put(key,(Serializable)result);
            }
        } else {
            // 不影响原来的流程
            result = joinPoint.proceed();
        }

        return result;
    }
}
