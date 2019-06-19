package com.qpg.aopdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.qpg.aop.bean.CancelBean;
import com.qpg.aop.bean.DenyBean;
import com.qpg.aop.trace.PermissionCanceledTrace;
import com.qpg.aop.trace.PermissionDeniedTrace;
import com.qpg.aop.trace.PermissionTrace;
import com.qpg.aop.util.SettingUtil;


public class RequestPermissionByUtil {


    @PermissionTrace(value = {Manifest.permission.CAMERA})
    public void requestPermission(Context context) {
        Toast.makeText(context, "相机权限申请成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限被取消
     *
     * @param bean CancelBean
     */
    @PermissionCanceledTrace
    public void dealCancelPermission(CancelBean bean) {
        Toast.makeText(bean.getContext(), "相机权限申请被取消，请求码 :" + bean.getRequestCode(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 权限被拒绝
     *
     * @param denyBean DenyBean
     */
    @PermissionDeniedTrace
    public void dealDeniedPermission(DenyBean denyBean) {

        final Context context = denyBean.getContext();

        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("相机权限被禁止，需要手动打开")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SettingUtil.go2Setting(context);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

    }


}
