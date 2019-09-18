package com.qpg.aop.aspect;

import android.text.TextUtils;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.DeleteSpTrace;
import com.qpg.aop.util.SPUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class DeletePrefsAspect {

    @Pointcut("execution(@com.qpg.aop.trace.DeleteSpTrace * *(..))")
    public void onPrefsDeleteMethod() {
    }

    @Around("onPrefsDeleteMethod() && @annotation(deleteSpTrace)")
    public Object doPrefsDeleteMethod(final ProceedingJoinPoint joinPoint, DeleteSpTrace deleteSpTrace) throws Throwable {
        Object result = null;
        if (deleteSpTrace!=null) {
            String key = deleteSpTrace.key();
            boolean allEntries = deleteSpTrace.allEntries();
            result = joinPoint.proceed();
            if (allEntries){
                if (!TextUtils.isEmpty(key))
                    throw new IllegalArgumentException("Key cannot have value when cleaning all caches");
                SPUtil.clear(SuperAop.getInstance().getContext());
            }
            SPUtil.remove(SuperAop.getInstance().getContext(), key);
        } else {
            // 不影响原来的流程
            result = joinPoint.proceed();
        }

        return result;
    }

}
