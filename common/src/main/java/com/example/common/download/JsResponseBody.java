package com.example.common.download;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Liszt on 2019/4/8.
 */

public class JsResponseBody extends ResponseBody {
    private ResponseBody responseBody;
    private JsDownloadListerner downloadListerner;

    private BufferedSource bufferedSource;//输入流，此处当InputSteam 使用
    private final long contentLength;

    public JsResponseBody(ResponseBody body, JsDownloadListerner listerner) {
        responseBody = body;
        downloadListerner = listerner;
        contentLength = responseBody.contentLength();
        downloadListerner.onStartDownload(responseBody.contentLength());
    }


    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (null != downloadListerner) {
                    if (bytesRead != -1) {
//                        downloadListerner.onProgress((int) totalBytesRead);
                        downloadListerner.onProgress(getProgress(totalBytesRead));
                    }
                }
                return bytesRead;
            }
        };
    }

    private int getProgress(long currentReded) {
        int progress = (int) ((currentReded * 1.0 / contentLength) * 100);
        return progress;
    }
}
