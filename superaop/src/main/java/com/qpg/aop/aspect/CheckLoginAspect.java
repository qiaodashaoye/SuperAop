package com.qpg.aop.aspect;

import android.app.Activity;
import android.content.Context;

import com.qpg.aop.SuperAop;
import com.qpg.aop.intf.ILogin;
import com.qpg.aop.trace.CheckLoginTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CheckLoginAspect {
    private Activity mStartActivity;

    //比作切蛋糕，如何切蛋糕
    //第一步获切点，即获得想要处理方法：* *代表所有方法，（..）代表所有参数，这里可以根据具体的方法类型来做处理
    @Pointcut("execution(@com.qpg.aop.trace.CheckLoginTrace  * *(..))")
    public void executionCheckLogin() {
    }

    //对于想好切的蛋糕，如何吃
    //第二步处理获取的切点
    @Around("executionCheckLogin()")
    public void checkLogin(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ILogin login = SuperAop.getInstance().getLogin();
        if (login == null) {
            throw new ExceptionInInitializerError("ILogin没有初始化！");
        }
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        CheckLoginTrace checkLogin = signature.getMethod().getAnnotation(CheckLoginTrace.class);
        if (checkLogin != null) {
            Context context = (Context) proceedingJoinPoint.getThis();

            if (login.isLogin(context)) {
                proceedingJoinPoint.proceed();
            } else {
                login.login(context, checkLogin.actionDefine());
            }
        }
    }
}
