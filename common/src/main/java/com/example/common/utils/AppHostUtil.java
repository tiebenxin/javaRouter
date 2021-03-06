package com.example.common.utils;

import android.text.TextUtils;

import com.example.common.BuildConfig;


/**
 * 提供HostApi
 */
public class AppHostUtil {

    private static final String PORT_9999 = ":9999";//tcp端口
    private static final String PORT_8888 = ":8888";//http端口：8686，  https端口：8888
    private static final String HTTP = "https://";

    private static String connectHostApi;

    private final static String getConnectHostApi() {
//        if (isEmpty()) {
//            connectHostApi = BuildConfig.API_HOST;
//        }
        connectHostApi = BuildConfig.API_HOST;

//        if (isEmpty()) {
//            throw new NullPointerException("请检查config.gradle#host配置");
//        }
        return connectHostApi;
    }

    public final static String getHttpConnectHostApi() {
//        if (isEmpty()) {
            getConnectHostApi();
//        }
        return HTTP + connectHostApi + PORT_8888;
    }

    public final static String getTcpConnectHostApi() {
        if (isEmpty()) {
            getConnectHostApi();
        }
        return connectHostApi + PORT_9999;
    }

    private static boolean isEmpty() {
        return TextUtils.isEmpty(connectHostApi) || connectHostApi.equals("null");
    }
}
