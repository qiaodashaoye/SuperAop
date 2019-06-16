package com.qpg.aop.aspect;

import com.qpg.aop.trace.TitleBarTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class TitleBarAspect {
    //比作切蛋糕，如何切蛋糕
    //第一步获切点，即获得想要处理方法：* *代表所有方法，（..）代表所有参数，这里可以根据具体的方法类型来做处理
    @Pointcut("execution(@com.qpg.aop.trace.TitleBarTrace  * *(..))")
    public void executionTitleBar() {
    }

    //对于想好切的蛋糕，如何吃
    //第二步处理获取的切点
    @Around("executionTitleBar()")
    public Object checkNet(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        TitleBarTrace checkNet = signature.getMethod().getAnnotation(TitleBarTrace.class);
        if (checkNet != null) {

        }
        return proceedingJoinPoint.proceed();

    }


}
