package com.example.common.api.demo;

import com.example.common.api.http.FXRequestManager;
import com.example.common.api.http.FXRxSubscriberHelper;
import com.example.common.api.rx.RxSchedulers;
import com.example.common.bean.GetVersionInfoResult;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Liszt on 2019/4/6.
 */

public class SystemApi {

    public interface Api{
        @POST("/xdata-proxy/v1/client/getAppVersion")
        Observable<GetVersionInfoResult> getVersionInfo(@Query("appid") String appid,
                                                        @Query("appclient") String appclient);
    }

    private Api api;

    public SystemApi() {
        api = FXRequestManager.getRequest(Api.class);
    }

    /**
     * 获取最新版本信息
     *
     * @param rxSubscriberHelper
     */
    public void getVersionInfo(FXRxSubscriberHelper<GetVersionInfoResult> rxSubscriberHelper) {
//        api.getVersionInfo("com.feige.fingerchat", "android")
//                .compose(RxSchedulers.handleResult())
//                .compose(RxSchedulers.rxSchedulerHelper())
//                .subscribe(rxSubscriberHelper);
    }
}
