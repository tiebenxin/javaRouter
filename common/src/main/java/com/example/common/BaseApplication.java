package com.example.common;

import android.app.Application;
import android.util.Log;

import com.example.common.utils.ContextHelper;

/**
 * Created by Liszt on 2018/9/21.
 */

public class BaseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("app create", "");
    ContextHelper.setContext(this);
  }
}
