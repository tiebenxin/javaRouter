package com.example.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.dialog.CommenProgressDialog;
import com.example.common.download.JsDownloadListerner;
import com.example.common.okhttp.DownloadMananger;
import com.example.main.databinding.ActivityMainBinding;

/**
 * Created by Liszt on 2018/9/22.
 */

@Route(path = "/main/main")
public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Button bt_login;
    private Button bt_chat;
    private Button bt_check;
    private CommenProgressDialog progressDialog;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                progressDialog = new CommenProgressDialog(MainActivity.this, R.style.LoadingDialog, "下载进度为" + 0 + "%");
                progressDialog.show();
            } else if (msg.what == 1) {
                progressDialog.setMessage(msg.arg1 + "%");
            } else if (msg.what == 2) {
                progressDialog.dismiss();
            }
        }
    };
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bt_login = findViewById(R.id.bt_login);
        bt_chat = findViewById(R.id.bt_chat);
        bt_check = findViewById(R.id.bt_check);
        bt_login.setOnClickListener(this);
        bt_chat.setOnClickListener(this);
        bt_check.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_chat) {
            toChat();
        } else if (i == R.id.bt_login) {
//            toLogin();
            binding.dlParent.toggle();

        } else if (i == R.id.bt_check) {
            check();
        }
    }

    private void check() {
        downloadApp();
    }

    public void toLogin() {
        ARouter.getInstance().build("/login/login").navigation();
    }

    public void toChat() {
        ARouter.getInstance().build("/chat/chat").navigation();
    }


    private void downloadApp() {
//        final String url = "http://10.3.9.200/pack/android/im2/IM_327s.apk";
        final String url = "http://feige.hnlens.com/pack/android/im2/IM_327s.apk";//内网可用
//        final String url = "http://www.hnlens.com/feige/pack/IM_327s.apk";//外网可用?


        new Thread(new Runnable() {
            @Override
            public void run() {
                String file = Environment.getExternalStorageDirectory() + "/327s.apk";

                DownloadMananger mananger = new DownloadMananger(new JsDownloadListerner() {
                    @Override
                    public void onStartDownload(long length) {
                        System.out.println("开始下载：" + "length = " + length);
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);

                    }

                    @Override
                    public void onProgress(int progress) {
                        System.out.println("进度：" + "progress = " + progress);
                        if (progress == 100) {
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.arg1 = progress;
                            handler.sendMessage(msg);
                        }

                    }

                    @Override
                    public void onFail(String message) {
                        System.out.println("下载失败：" + message);
                        progressDialog.dismiss();

                    }
                });
                mananger.downloadApp(url, file);

            }
        }).start();
    }

}
