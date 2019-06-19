package com.qpg.aop.aspect;

import android.text.TextUtils;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.InterceptTrace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class InterceptAspect {
    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.InterceptTrace * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void onInterceptMethod() {
    }

    @Around("onInterceptMethod() && @annotation(intercept)")
    public Object doInterceptMethod(ProceedingJoinPoint joinPoint, InterceptTrace intercept) throws Throwable {
        if (SuperAop.getInterceptor() == null)return joinPoint.proceed();
        String value = intercept.value();
        if (!TextUtils.isEmpty(value)){
            //拦截
            boolean result = proceedIntercept(intercept.value(), joinPoint);
            return result ? null : joinPoint.proceed();
        }
        return joinPoint.proceed();
    }

    private boolean proceedIntercept(String value, ProceedingJoinPoint joinPoint) throws Throwable {
        boolean intercept = SuperAop.getInterceptor().intercept(value, joinPoint.getSignature().getName());
        if (intercept){
            return true;
        }
        return false;
    }
}
