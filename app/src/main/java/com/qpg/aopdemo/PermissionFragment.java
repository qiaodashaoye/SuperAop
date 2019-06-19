package com.qpg.aopdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.qpg.aop.bean.CancelBean;
import com.qpg.aop.bean.DenyBean;
import com.qpg.aop.trace.PermissionCanceledTrace;
import com.qpg.aop.trace.PermissionDeniedTrace;
import com.qpg.aop.trace.PermissionTrace;
import com.qpg.aop.util.SettingUtil;

public class PermissionFragment extends Fragment {

    private Button btn_permission;

    public PermissionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permission, container, false);
        btn_permission = (Button) view.findViewById(R.id.btn_permission);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    @PermissionTrace(value = {Manifest.permission.CALL_PHONE})
    private void requestPermission() {
        Toast.makeText(getActivity(), "电话权限申请通过", Toast.LENGTH_SHORT).show();
    }

    @PermissionDeniedTrace
    public void dealDenyPermission(DenyBean bean) {
        Toast.makeText(getActivity(), "电话权限申请被禁止", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("电话权限被禁止，需要手动打开")
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SettingUtil.go2Setting(getActivity());
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

    @PermissionCanceledTrace
    public void dealCancelPermission(CancelBean bean) {
        Toast.makeText(getActivity(), "电话权限申请被取消", Toast.LENGTH_SHORT).show();
    }
}
