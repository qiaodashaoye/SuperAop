package com.qpg.aop.aspect;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.PrefsTrace;
import com.qpg.aop.util.SPUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class PrefsAspect {

    @Around("execution(!synthetic * *(..)) && onPrefsMethod()")
    public Object doPrefsMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
        return prefsMethod(joinPoint);
    }

    @Pointcut("@within(com.qpg.aop.trace.PrefsTrace)||@annotation(com.qpg.aop.trace.PrefsTrace)")
    public void onPrefsMethod() {
    }

    private Object prefsMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        PrefsTrace prefs = method.getAnnotation(PrefsTrace.class);
        Object result = null;
        if (prefs!=null) {
            String key = prefs.key();

            result = joinPoint.proceed();
            String type = ((MethodSignature) joinPoint.getSignature()).getReturnType().toString();

            if (!"void".equalsIgnoreCase(type)) {
                SPUtil.put(SuperAop.getInstance().getContext(), key, result);
            }
        } else {
            // 不影响原来的流程
            result = joinPoint.proceed();
        }

        return result;
    }
}
