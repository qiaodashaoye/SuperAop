package com.qpg.aop.aspect;

import android.app.Fragment;
import android.content.Context;

import com.qpg.aop.PermissionRequestActivity;
import com.qpg.aop.SuperAop;
import com.qpg.aop.bean.CancelBean;
import com.qpg.aop.bean.DenyBean;
import com.qpg.aop.interf.IPermission;
import com.qpg.aop.trace.PermissionCanceledTrace;
import com.qpg.aop.trace.PermissionDeniedTrace;
import com.qpg.aop.trace.PermissionTrace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 权限切面Aspect类
 */
@Aspect
public class PermissionAspect {

    Context context;

    private static final String PERMISSION_REQUEST_POINTCUT =
            "execution(@com.qpg.aop.trace.PermissionTrace * *(..))";

    @Pointcut(PERMISSION_REQUEST_POINTCUT + " && @annotation(permissionTrace)")
    public void requestPermissionMethod(PermissionTrace permissionTrace) {
    }

    @Around("requestPermissionMethod(permissionTrace)")
    public void AroundJoinPoint(final ProceedingJoinPoint joinPoint, PermissionTrace permissionTrace) {

        final Object object = joinPoint.getThis();
        if (object == null) return;

        if (object instanceof Context) {
            context = (Context) object;
        } else if (object instanceof Fragment) {
            context = ((Fragment) object).getActivity();
        } else if (object instanceof androidx.fragment.app.Fragment) {
            context = ((androidx.fragment.app.Fragment) object).getActivity();
        } else {
            //获取切入点方法上的参数列表
            Object[] objects = joinPoint.getArgs();
            if (objects.length > 0) {
                //非静态方法且第一个参数为context
                if (objects[0] instanceof Context) {
                    context = (Context) objects[0];
                } else {
                    //没有传入context 默认使用application
                    context = SuperAop.getInstance().getContext();
                }
            } else {
                context = SuperAop.getInstance().getContext();
            }

        }

        if (context == null || permissionTrace == null) return;

        PermissionRequestActivity.PermissionRequest(context, permissionTrace.value(),
                permissionTrace.requestCode(), new IPermission() {
                    @Override
                    public void PermissionGranted() {
                        try {
                            joinPoint.proceed();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void PermissionDenied(int requestCode, List<String> denyList) {
                        Class<?> cls = object.getClass();
                        Method[] methods = cls.getDeclaredMethods();
                        if (methods.length == 0) return;
                        for (Method method : methods) {
                            //过滤不含自定义注解PermissionDenied的方法
                            boolean isHasAnnotation = method.isAnnotationPresent(PermissionDeniedTrace.class);
                            if (isHasAnnotation) {
                                method.setAccessible(true);
                                //获取方法类型
                                Class<?>[] types = method.getParameterTypes();
                                if (types.length != 1) return;
                                //获取方法上的注解
                                PermissionDeniedTrace aInfo = method.getAnnotation(PermissionDeniedTrace.class);
                                if (aInfo == null) return;
                                //解析注解上对应的信息
                                DenyBean bean = new DenyBean();
                                bean.setRequestCode(requestCode);
                                bean.setContext(context);
                                bean.setDenyList(denyList);
                                try {
                                    method.invoke(object, bean);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void PermissionCanceled(int requestCode) {
                        Class<?> cls = object.getClass();
                        Method[] methods = cls.getDeclaredMethods();
                        if (methods.length == 0) return;
                        for (Method method : methods) {
                            //过滤不含自定义注解PermissionCanceled的方法
                            boolean isHasAnnotation = method.isAnnotationPresent(PermissionCanceledTrace.class);
                            if (isHasAnnotation) {
                                method.setAccessible(true);
                                //获取方法类型
                                Class<?>[] types = method.getParameterTypes();
                                if (types.length != 1) return;
                                //获取方法上的注解
                                PermissionCanceledTrace aInfo = method.getAnnotation(PermissionCanceledTrace.class);
                                if (aInfo == null) return;
                                //解析注解上对应的信息
                                CancelBean bean = new CancelBean();
                                bean.setContext(context);
                                bean.setRequestCode(requestCode);
                                try {
                                    method.invoke(object, bean);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
    }

}
