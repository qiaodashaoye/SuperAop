package com.qpg.aop.aspect;

import android.util.Log;
import com.qpg.aop.trace.ScheduledTrace;
import com.qpg.aop.util.Preconditions;
import com.qpg.aop.util.reflect.Reflect;
import com.qpg.aop.util.reflect.ReflectException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@Aspect
public class ScheduledAspect {

    private static final String TAG = "ScheduledAspect";
    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.ScheduledTrace * *(..))";
    private Disposable disposable;

    @Pointcut(POINTCUT_METHOD)
    public void onScheduledMethod() {
    }

    @Around("onScheduledMethod() && @annotation(scheduled)")
    public Object doScheduledMethod(final ProceedingJoinPoint joinPoint, ScheduledTrace scheduled) throws Throwable {

        long initialDelay = scheduled.initialDelay();
        long interval = scheduled.interval();
        final int counts = scheduled.count();
        TimeUnit timeUnit = scheduled.timeUnit();
        final String taskExpiredCallback = scheduled.taskExpiredCallback();
        Object result = null;
        disposable = Observable.interval(initialDelay+interval, interval, timeUnit)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        try {
                            joinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        return aLong + 1;
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        Log.d(TAG, "count: " + count);
                        if (count == (counts-1)) {
                            if (disposable != null) {
                                disposable.dispose();
                            }
                            doTaskExpiredCallback(joinPoint, taskExpiredCallback);
                        }
                    }
                });

        result = joinPoint.proceed();
        return result;
    }

    private void doTaskExpiredCallback(ProceedingJoinPoint joinPoint, String taskExpiredCallback){
        if (Preconditions.isNotBlank(taskExpiredCallback)) {

            try {
                Reflect.on(joinPoint.getTarget()).call(taskExpiredCallback);
            } catch (ReflectException exception) {
                exception.printStackTrace();
                Log.e(TAG, "no method "+taskExpiredCallback);
            }
        }
    }

}
