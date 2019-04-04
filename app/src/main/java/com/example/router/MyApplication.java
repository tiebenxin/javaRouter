package com.example.router;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.common.BaseApplication;

/**
 * Created by Liszt on 2018/9/21.
 */

public class MyApplication extends BaseApplication {

  @Override
  public void onCreate() {
    super.onCreate();
    initRouter();

  }

  public void initRouter() {
    //开启InstantRun之后，一定要在ARouter.init之前调用openDebug
    ARouter.openDebug();
    ARouter.openLog();
    ARouter.init(this);
  }
}
