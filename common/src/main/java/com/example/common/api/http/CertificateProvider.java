package com.example.common.api.http;

import android.text.TextUtils;


import com.example.common.utils.ContextHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * 证书provider
 * Created by Liszt on 2018/4/11.
 */

public class CertificateProvider {

    /**
     * 根据URL的地址来判断证书的加载
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static InputStream[] getCertificateStreams(String url) throws IOException {
        String cer_name = null;
        if (url == null) {
            cer_name = "";
        } else {
            if (url.startsWith("")) {
                cer_name = "";
            }
        }

        if (!TextUtils.isEmpty(cer_name)) {
            return new InputStream[]{ContextHelper.getContext().getAssets().open(cer_name)};
        } else {
            return null;
        }
    }
}
