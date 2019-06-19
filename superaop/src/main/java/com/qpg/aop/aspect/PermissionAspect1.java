package com.qpg.aop.aspect;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.qpg.aop.trace.PermissionTrace;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class PermissionAspect1 {

    private static final String POINTCUT_METHOD = "execution(@com.qpg.widget.aop.trace.PermissionTrace * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithPermission() {
    }

    @Around("methodAnnotatedWithPermission()")
    public Object permissionMethod(final ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        PermissionTrace permission = method.getAnnotation(PermissionTrace.class);

        String[] permissions = permission.value();
        int requestCode = permission.requestCode();

        Object object = joinPoint.getThis();

        Context context = null;

        if (object instanceof Activity) {
            context = (Activity) object;
        } else if (object instanceof FragmentActivity) {
            context = (FragmentActivity) object;
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getContext();
        } else if (object instanceof Service) {
            context = (Service) object;
        }

        Object o = null;

        if (checkPermissions(context, permissions)) {
            o = joinPoint.proceed();
        } else {

            Intent intent = new Intent();
            intent.setClass(context, PermissionActivity.class);
            intent.putExtra("permissions", permissions);
            intent.putExtra("requestcode", requestCode);
            context.startActivity(intent);
        }

        return o;
    }

    private boolean checkPermission(Context context, String permission) {

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean checkPermissions(Context context, String[] permissions) {

        for(String permission : permissions) {
            if (!checkPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

}
