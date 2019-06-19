package com.qpg.aop.interf;

import android.content.Context;

public interface ILogin {
    /**
     * 登录事件接收
     * @param context Context
     * @param actionDefine 登录操作
     */
    void login(Context context, int actionDefine);

    /**
     * 判断是否登录
     * @param context Context
     * @return
     */
    boolean isLogin(Context context);
}
