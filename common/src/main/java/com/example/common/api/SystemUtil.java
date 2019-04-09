package com.example.common.api;


import com.example.common.BuildConfig;

/**
 * Created by Liszt on 2017/3/8.
 */

public class SystemUtil {

    /**
     * 是否支持log输出
     */
    public static boolean isDeveloperLog = false;

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static boolean isCanLog() {
        return BuildConfig.DEBUG || isDeveloperLog;
    }

    public static void updateDeveloperLogControl(boolean b) {
        isDeveloperLog = b;
    }
}
