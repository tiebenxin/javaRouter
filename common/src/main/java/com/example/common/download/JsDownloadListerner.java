package com.example.common.download;

/**
 * Created by Liszt on 2019/4/8.
 */

public interface JsDownloadListerner {

    void onStartDownload(long length);

    void onProgress(int progress);

    void onFail(String message);
}
