package com.qpg.aopdemo;

import android.app.Application;

import com.qpg.aop.SuperAop;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SuperAop.getInstance().init(this);
    }

}
