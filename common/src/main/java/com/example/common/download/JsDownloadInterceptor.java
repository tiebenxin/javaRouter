package com.example.common.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Liszt on 2019/4/8.
 */

public class JsDownloadInterceptor implements Interceptor {
    private JsDownloadListerner downloadListerner;

    public JsDownloadInterceptor(JsDownloadListerner listerner) {
        downloadListerner = listerner;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new JsResponseBody(response.body(), downloadListerner)).build();
    }
}
