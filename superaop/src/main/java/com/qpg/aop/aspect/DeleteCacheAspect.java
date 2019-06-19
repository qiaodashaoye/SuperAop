package com.qpg.aop.aspect;

import android.text.TextUtils;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.DeleteCacheTrace;
import com.qpg.aop.util.ACache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class DeleteCacheAspect {
    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.DeleteCacheTrace * *(..))";

    //切点位置，所有的DeleteCacheTrace处
    @Pointcut(POINTCUT_METHOD)
    public void onCacheDeleteMethod() {
    }

    //环绕处理，并拿到DeleteCacheTrace注解值
    @Around("onCacheDeleteMethod() && @annotation(deleteCacheTrace)")
    public Object doCacheDeleteMethod(ProceedingJoinPoint joinPoint, DeleteCacheTrace deleteCacheTrace) throws Throwable {
        String key = deleteCacheTrace.key();
        boolean beforeInvocation = deleteCacheTrace.beforeInvocation();
        boolean allEntries = deleteCacheTrace.allEntries();
        ACache aCache = ACache.get(SuperAop.getInstance().getContext());
        Object result = null;
        if (allEntries){
            //如果是全部清空，则key不需要有值
            if (!TextUtils.isEmpty(key))
                throw new IllegalArgumentException("Key cannot have value when cleaning all caches");
            aCache.clear();
        }
        if (beforeInvocation){
            //方法执行前，移除缓存
            aCache.remove(key);
            result = joinPoint.proceed();
        }else {
            //方法执行后，移除缓存，如果出现异常缓存就不会清除（推荐）
            result = joinPoint.proceed();
            aCache.remove(key);
        }
        return result;
    }

}
