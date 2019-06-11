package com.example.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.bean.UserBean;
import com.example.common.utils.RandomVerifyCode;
import com.example.common.wight.VerifyCodeView;
import com.example.common.wight.VerifyKTView;

@Route(path = "/login/login")
public class LoginActivity extends AppCompatActivity {

    private RandomVerifyCode randomVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button bt_main = findViewById(R.id.bt_main);
        bt_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });

        ImageView iv_code = findViewById(R.id.iv_code);
        randomVerifyCode = new RandomVerifyCode(this, iv_code, 4, 3, 20);
        System.out.println(randomVerifyCode.getCode());
        iv_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomVerifyCode.refresh();
            }
        });

        final VerifyCodeView iv_verify = findViewById(R.id.iv_verify);
        iv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_verify.setRefresh(true);
                iv_verify.refresh();
            }
        });

        final VerifyKTView iv_kt = findViewById(R.id.iv_verifyKt);
        iv_kt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_kt.setRefresh(true);
                iv_kt.refresh();
            }
        });


    }

    public void toMain() {
        ARouter.getInstance().build("/main/main").navigation();
    }

}
