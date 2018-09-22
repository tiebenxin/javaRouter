package com.example.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.common.bean.UserBean;

@Route(path = "/login/login")
public class LoginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
  }

  private void getUserBean() {
    UserBean.Builder builder = UserBean.newBuilder();
    builder.setUserId("111").setUserName("jjj").setUserRemarkName("").setSex(0)
        .setTime(System.currentTimeMillis());
    UserBean bean = builder.builder();
  }
}
