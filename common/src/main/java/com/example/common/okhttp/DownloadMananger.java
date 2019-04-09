package com.example.common.okhttp;

import com.example.common.download.JsDownloadInterceptor;
import com.example.common.download.JsDownloadListerner;
import com.example.common.utils.AppHostUtil;

import org.reactivestreams.Subscriber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Liszt on 2019/4/8.
 */

public class DownloadMananger {
    private static final int DEFAULT_TIMEOUT = 15;

    private Retrofit retrofit;
    private JsDownloadListerner downloadListerner;

    public interface Api {

        /**
         * 下载视频
         *
         * @param fileUrl 视频路径
         * @return
         */
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String fileUrl);
    }

    public DownloadMananger(JsDownloadListerner listerner) {
        downloadListerner = listerner;
        retrofit = createRetrofit(listerner);

    }

    private Retrofit createRetrofit(JsDownloadListerner listerner) {
        JsDownloadInterceptor interceptor = new JsDownloadInterceptor(listerner);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppHostUtil.getHttpConnectHostApi())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public void downloadApp(String url, final String file) {
        retrofit.create(Api.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())//用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream input) throws Exception {
                        saveFile(input, file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {

                    }
                });
    }

    private void saveFile(final InputStream inputStream, final String file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File f= null;
                try {
                    f = new File(file);
                    if (f.exists()) {
                        f.delete();
                        f.createNewFile();
                    } else {
                        f.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (f == null){
                    return;
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = inputStream.read(b)) != -1) {
                        fos.write(b, 0, len);
                    }
                    inputStream.close();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    downloadListerner.onFail("FileNotFoundException");
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadListerner.onFail("IOException");

                }


            }
        }).start();
    }


}
