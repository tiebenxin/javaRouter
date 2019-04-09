package com.example.common.utils;

import android.content.Context;

/**
 * Created by Liszt on 2019/4/6.
 */

public class ContextHelper {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context c) {
        context = c;
    }
}
