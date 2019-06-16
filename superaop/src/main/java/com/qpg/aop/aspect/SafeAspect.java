package com.qpg.aop.aspect;

import com.qpg.aop.trace.SafeTrace;
import com.qpg.aop.util.Preconditions;
import com.qpg.aop.util.reflect.Reflect;
import com.qpg.aop.util.reflect.ReflectException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

@Aspect
public class SafeAspect {

    private static final String POINTCUT_METHOD = "execution(@com.qpg.aop.trace.SafeTrace * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithSafe() {
    }

    @Around("methodAnnotatedWithSafe()")
    public Object safeMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SafeTrace safe = method.getAnnotation(SafeTrace.class);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
         //   L.w(getStringFromException(e));

            String callBack = safe.callBack();

            if (Preconditions.isNotBlank(callBack)) {

                try {
                    Reflect.on(joinPoint.getTarget()).call(callBack);
                } catch (ReflectException exception) {
                    exception.printStackTrace();
                   // L.e("no method "+callBack);
                }
            }
        }
        return result;
    }

    private static String getStringFromException(Throwable ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }
}
