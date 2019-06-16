package com.qpg.aop.aspect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.qpg.aop.SuperAop;
import com.qpg.aop.trace.CheckNetTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CheckNetAspect {
    //比作切蛋糕，如何切蛋糕
    //第一步获切点，即获得想要处理方法：* *代表所有方法，（..）代表所有参数，这里可以根据具体的方法类型来做处理
    @Pointcut("execution(@com.qpg.aop.trace.CheckNetTrace  * *(..))")
    public void executionCheckNet() {
    }

    //对于想好切的蛋糕，如何吃
    //第二步处理获取的切点
    @Around("executionCheckNet()")
    public void checkNet(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        CheckNetTrace checkNet = signature.getMethod().getAnnotation(CheckNetTrace.class);
        if (checkNet != null) {
            if (hasInternet()) {
                proceedingJoinPoint.proceed();
            } else {
                Toast.makeText(SuperAop.getInstance().getContext(),"网络未连接",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) SuperAop.getInstance().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }
}
