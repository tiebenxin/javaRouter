package com.example.common.api.demo;

import com.example.common.api.http.FXRxSubscriberHelper;
import com.example.common.bean.GetVersionInfoResult;
import com.example.common.bean.VersionInfoBean;

import org.junit.Test;

/**
 * Created by Liszt on 2019/4/6.
 */
public class ApiTestTest {

    @Test
    public void test(){
        new SystemApi().getVersionInfo(new FXRxSubscriberHelper<GetVersionInfoResult>() {
            @Override
            public void _onNext(GetVersionInfoResult getVersionInfoResult) {
                VersionInfoBean bean = getVersionInfoResult.getContent();
                if (bean == null) {
                    return;
                }
                System.out.println(bean.toString());

            }
        });
    }

}