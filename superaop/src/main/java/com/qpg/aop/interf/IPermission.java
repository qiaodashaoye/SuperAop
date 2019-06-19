package com.qpg.aop.interf;

import java.util.List;

public interface IPermission {

    //同意权限
    void PermissionGranted();

    //拒绝权限并且选中不再提示
    void PermissionDenied(int requestCode, List<String> denyList);

    //取消权限
    void PermissionCanceled(int requestCode);
}
