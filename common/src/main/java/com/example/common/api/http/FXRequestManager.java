package com.example.common.api.http;


import com.example.common.api.RequestManager;
import com.example.common.utils.AppHostUtil;

/**
 * Created by Liszt on 2018/4/11
 */
public class FXRequestManager extends RequestManager {

    public static <T> T getRequest(Class<T> clazz) {
        T t = (T) sRequestManager.get(clazz);
        if (t == null) {
            t = FXRetrofitClient.createApi(clazz, AppHostUtil.getHttpConnectHostApi());
            sRequestManager.put(clazz, t);
        }
        return t;
    }

    public static <T> T getCiscoRequest(Class<T> clazz) {
        T t = (T) sRequestManager.get(clazz);
        if (t == null) {
            t = FXRetrofitClient.createApi(clazz,"http://10.3.7.180:8080");
            sRequestManager.put(clazz, t);
        }
        return t;
    }
}
