package com.qpg.aop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.qpg.aop.interf.IPermission;
import com.qpg.aop.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class PermissionRequestActivity extends Activity {

    private static IPermission permissionListener;
    private String[] permissions;
    private static final String PERMISSION_KEY = "permission_key";
    private static final String REQUEST_CODE = "request_code";
    private int requestCode;

    /**
     * 跳转到Activity申请权限
     *
     * @param context     Context
     * @param permissions Permission List
     * @param iPermission Interface
     */
    public static void PermissionRequest(Context context, String[] permissions, int requestCode, IPermission iPermission) {
        permissionListener = iPermission;
        Intent intent = new Intent(context, PermissionRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_KEY, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            permissions = bundle.getStringArray(PERMISSION_KEY);
            requestCode = bundle.getInt(REQUEST_CODE, 0);
        }
        if (permissions == null || permissions.length <= 0) {
            finish();
            return;
        }
        requestPermission(permissions);
    }


    /**
     * 申请权限
     *
     * @param permissions permission list
     */
    private void requestPermission(String[] permissions) {

        if (PermissionUtil.hasSelfPermissions(this, permissions)) {
            //all permissions granted
            if (permissionListener != null) {
                permissionListener.PermissionGranted();
                permissionListener = null;
            }
            finish();
            overridePendingTransition(0, 0);
        } else {
            //request permissions
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (PermissionUtil.verifyPermissions(grantResults)) {
            //所有权限都同意
            if (permissionListener != null) {
                permissionListener.PermissionGranted();
            }
        } else {
            if (!PermissionUtil.shouldShowRequestPermissionRationale(this, permissions)) {
                //权限被拒绝并且选中不再提示
                if (permissions.length != grantResults.length) return;
                List<String> denyList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        denyList.add(permissions[i]);
                    }
                }
                if (permissionListener != null) {
                    permissionListener.PermissionDenied(requestCode, denyList);
                }
            } else {
                //权限被取消
                if (permissionListener != null) {
                    permissionListener.PermissionCanceled(requestCode);
                }
            }

        }
        permissionListener = null;
        finish();
        overridePendingTransition(0, 0);
    }

}
