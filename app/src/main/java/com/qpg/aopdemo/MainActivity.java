package com.qpg.aopdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.qpg.aop.trace.AsyncTrace;
import com.qpg.aop.trace.CacheTrace;
import com.qpg.aop.trace.CheckLoginTrace;
import com.qpg.aop.trace.CheckNetTrace;
import com.qpg.aop.trace.DelayAwayTrace;
import com.qpg.aop.trace.DelayTrace;
import com.qpg.aop.trace.DeleteCacheTrace;
import com.qpg.aop.trace.DeleteSpTrace;
import com.qpg.aop.trace.PrefsTrace;
import com.qpg.aop.trace.RetryTrace;
import com.qpg.aop.trace.SafeTrace;
import com.qpg.aop.trace.ScheduledTrace;
import com.qpg.aop.trace.SingleClickTrace;
import com.qpg.aop.util.ACache;
import com.qpg.aop.util.SPUtil;
import com.qpg.aopdemo.bean.User;

//1、gradlew install    2、gradlew bintrayUpload
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveUserByCache();
        getUserByCache();
        saveUserByPrefs();
        getUserByByPrefs();
        noTry();
        retry();
    }


    @SingleClickTrace()
    private void setActionBar(int y) {

    }

    @CheckLoginTrace(actionDefine = 1)
    private void asd() {

    }

    @CheckNetTrace()
    private void net() {

    }

    //  @CacheTrace(key = "user",expiry = 2000)//自定义缓存过期时间
    @CacheTrace(key = "user")
    private User saveUserByCache() {
        User userInfo = new User();
        userInfo.setUid("111");
        userInfo.setToken("sfsdgwefc");
        userInfo.setName("乔少");
        return userInfo;
    }

    void getUserByCache() {
        ACache cache = ACache.get(this);
        User user = (User) cache.getAsObject("user");
        System.out.println(user);
    }
    //移除缓存数据
    @DeleteCacheTrace(key = "user")
    private void removeUser() {

    }

    @PrefsTrace(key = "user")
    private User saveUserByPrefs() {
        User userInfo = new User();
        userInfo.setUid("111");
        userInfo.setToken("sfsdgwefc");
        userInfo.setName("乔少");
        return userInfo;
    }

    void getUserByByPrefs() {
        User user = SPUtil.get(this, "user", null);
        System.out.println(user);
    }

    @DeleteSpTrace(key = "user")
    public void removeUserBySP() {
    }

    @SafeTrace(callBack = "throwMethod")
    private void noTry() {
        int a = 10 / 0;
    }

    @RetryTrace(count = 3, delay = 1000, asyn = true, retryCallback = "retryCallback")
    public void retry() {
    }

    @AsyncTrace
    public void asyn() {
    }

    @ScheduledTrace(interval = 1000L, count = 10, taskExpiredCallback = "taskExpiredCallback")
    public void scheduled() {
    }

    //开启延迟任务（10s后执行该方法）
    @DelayTrace(key = "test", delay = 10000L)
    public void delay() {
    }

    //移除延迟任务
    @DelayAwayTrace(key = "test")
    public void cancelDelay() {
    }

    private void taskExpiredCallback() {
    }

    private void throwMethod(Throwable throwable) {
    }
}
