package com.qpg.aop.aspect;

import android.text.TextUtils;
import com.qpg.aop.trace.DelayTrace;
import com.qpg.aop.util.collection.NoEmptyHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * description 延迟任务
 */
@Aspect
public class DelayAspect {
    private static final String TAG = "DelayAspect";
    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.DelayTrace * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void onDelayMethod() {
    }

    @Around("onDelayMethod() && @annotation(delay)")
    public Object doDelayMethod(final ProceedingJoinPoint joinPoint, DelayTrace delay) throws Throwable {
        String key = delay.key();
        if (TextUtils.isEmpty(key)){
            key = joinPoint.getSignature().getName();
        }
        long delayTime = delay.delay();
        TimeUnit unit = delay.timeUnit();
        Object result = null;
        if (delayTime>0) {
            final String finalKey = key;
            Disposable subscribe = Observable.timer(delayTime, unit)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            try {
                                joinPoint.proceed();
                                NoEmptyHashMap.getInstance().remove(finalKey);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    });
            NoEmptyHashMap.getInstance().put(key, subscribe);
        } else {
            result = joinPoint.proceed();
        }
        return result;
    }
}
