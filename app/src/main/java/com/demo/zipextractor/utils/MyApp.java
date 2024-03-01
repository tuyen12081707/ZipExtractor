package com.demo.zipextractor.utils;

import android.app.Application;
import android.content.Context;


public class MyApp extends Application {
    private static Context mContext;
    private static MyApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    public static synchronized MyApp getInstance() {
        MyApp myApp;
        synchronized (MyApp.class) {
            myApp = mInstance;
        }
        return myApp;
    }
}
