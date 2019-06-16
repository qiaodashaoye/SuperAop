package com.qpg.aop;

import android.app.Application;
import android.content.Context;

import com.qpg.aop.intf.ILogin;

public class SuperAop {
    private  Context mContext;
    private ILogin mILogin;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static SuperAop instance = null;

    public SuperAop init(Application context) {
        init(context,null);
        return this;
    }
    /**
     * 初始化
     *
     * @param context Context
     * @param iLogin  登录事件
     */
    public SuperAop init(Context context, ILogin iLogin) {
        if(mContext==null){
            mContext = context;
        }
        mILogin = iLogin;
        return this;
    }
    /*3.双重锁定:只在第一次初始化的时候加上同步锁*/
    public static SuperAop getInstance() {
        if (instance == null) {
            synchronized (SuperAop.class) {
                if (instance == null) {
                    instance = new SuperAop();
                }
            }
        }
        return instance;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new ExceptionInInitializerError("Please call SuperAop.init(this) in Application to initialize!");
        }
        return mContext;
    }
    public ILogin getLogin() {
        return mILogin;
    }
}
