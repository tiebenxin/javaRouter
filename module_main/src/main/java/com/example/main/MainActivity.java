package com.example.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by LL130386 on 2018/9/22.
 */

public class MainActivity extends AppCompatActivity implements OnClickListener {

  private Button bt_login;
  private Button bt_chat;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    bt_login = findViewById(R.id.bt_login);
    bt_chat = findViewById(R.id.bt_chat);
    bt_login.setOnClickListener(this);
    bt_chat.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.bt_chat) {
      toLogin();
    } else if (i == R.id.bt_login) {
      toLogin();
    }
  }

  public void toLogin() {
    ARouter.getInstance().build("/login/login").navigation();
  }

}
