package com.qpg.aopdemo;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.qpg.aop.bean.CancelBean;
import com.qpg.aop.bean.DenyBean;
import com.qpg.aop.trace.PermissionCanceledTrace;
import com.qpg.aop.trace.PermissionDeniedTrace;
import com.qpg.aop.trace.PermissionTrace;
import com.qpg.aop.util.SettingUtil;


public class PermissionService extends Service {

    public PermissionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestCamera();
            }
        }, 500);
        return super.onStartCommand(intent, flags, startId);
    }

    @PermissionTrace(value = {Manifest.permission.CAMERA})
    private void requestCamera() {
        Toast.makeText(PermissionService.this, "相机权限已经被同意", Toast.LENGTH_SHORT).show();
    }


    @PermissionDeniedTrace
    public void deniedCallBack(DenyBean bean) {
        Toast.makeText(PermissionService.this, "相机权限已经被禁止", Toast.LENGTH_SHORT).show();
        SettingUtil.go2Setting(PermissionService.this);
    }

    @PermissionCanceledTrace
    public void canceledCallBack(CancelBean bean) {
        Toast.makeText(PermissionService.this, "相机权限已经被取消", Toast.LENGTH_SHORT).show();
    }
}
