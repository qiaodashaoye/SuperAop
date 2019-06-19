package com.qpg.aop.aspect;

import com.qpg.aop.trace.DelayAwayTrace;
import com.qpg.aop.util.collection.NoEmptyHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import io.reactivex.disposables.Disposable;

@Aspect
public class DelayAwayAspect {
    private static final String TAG = "DelayAwayAspect";
    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.DelayAwayTrace * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void onDelayAwayMethod() {
    }

    @Around("onDelayAwayMethod() && @annotation(delayAway)")
    public Object doDelayAwayMethod(final ProceedingJoinPoint joinPoint, DelayAwayTrace delayAway) throws Throwable {
        String key = delayAway.key();
        Disposable subscribe = (Disposable) NoEmptyHashMap.getInstance().get(key);
        if (subscribe != null){
            subscribe.dispose();
            NoEmptyHashMap.getInstance().remove(key);
        }
        return joinPoint.proceed();
    }
}
