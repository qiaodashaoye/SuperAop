package com.qpg.aop.util;

import android.content.Context;
import android.os.Build;

import com.qpg.aop.interf.ISetting;
import com.qpg.aop.support.Default;
import com.qpg.aop.support.OPPO;
import com.qpg.aop.support.ViVo;


public class SettingUtil {

    private static final String MANUFACTURER_HUAWEI = "Huawei";//华为
    private static final String MANUFACTURER_MEIZU = "Meizu";//魅族
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米
    private static final String MANUFACTURER_SONY = "Sony";//索尼
    private static final String MANUFACTURER_OPPO = "OPPO";
    private static final String MANUFACTURER_LG = "LG";
    private static final String MANUFACTURER_VIVO = "vivo";
    private static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    private static final String MANUFACTURER_LETV = "Letv";//乐视
    private static final String MANUFACTURER_ZTE = "ZTE";//中兴
    private static final String MANUFACTURER_YULONG = "YuLong";//酷派
    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    /**
     * 跳设置界面
     *
     * @param context context
     */
    public static void go2Setting(Context context) {
        ISetting iSetting;

        switch (Build.MANUFACTURER) {
            case MANUFACTURER_VIVO:
                iSetting = new ViVo(context);
                break;
            case MANUFACTURER_OPPO:
                iSetting = new OPPO(context);
                break;
            default:
                iSetting = new Default(context);
                break;
        }
        if (iSetting.getSetting() == null) return;
        context.startActivity(iSetting.getSetting());
    }


}
