package com.qpg.aopdemo;

import android.app.Activity;
import android.os.Bundle;

import com.qpg.aop.trace.CacheTrace;
import com.qpg.aop.trace.CheckLoginTrace;
import com.qpg.aop.trace.CheckNetTrace;
import com.qpg.aop.trace.PrefsTrace;
import com.qpg.aop.trace.SafeTrace;
import com.qpg.aop.trace.SingleClickTrace;
import com.qpg.aopdemo.bean.User;
import com.safframework.cache.Cache;
import com.safframework.prefs.AppPrefs;

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
        Cache cache = Cache.get(this);
        User user = (User) cache.getObject("user");
        System.out.println(user);
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
        AppPrefs appPrefs = AppPrefs.get(this);
        User user = (User) appPrefs.getObject("user");
        System.out.println(user);
    }

    @SafeTrace
    private void noTry() {
       // int a = 10 / 0;
    }
}
