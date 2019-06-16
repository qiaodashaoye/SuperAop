package com.qpg.aop.aspect;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qpg.aop.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends Activity {

    private String[] permissions;
    private int requestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        permissions = intent.getStringArrayExtra("permissions");
        requestCode = intent.getIntExtra("requestcode", 0);
        setContentView(R.layout.activity_permission);
        if (permissions != null && permissions.length > 0) {
            requestPermission(permissions);
        }
    }

    private void requestPermission(String[] permissions) {

        List<String> failure = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                failure.add(permission);
            }
        }
        if (failure.size() == 0) {
            requestPermissionSuccess();
            return;
        }

        ActivityCompat.requestPermissions(this, failure.toArray(new String[]{}), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
        if (requestCode == this.requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                requestPermissionSuccess();
            } else {

                boolean alwaysHidePermission = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断是否勾选禁止后不再询问
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (!showRequestPermission) {
                            alwaysHidePermission = true;
                        }
                    }
                }

                requestPermissionFailed();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestPermissionSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    private void requestPermissionFailed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
