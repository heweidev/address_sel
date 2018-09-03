package com.hewei.addressselect;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by fengyinpeng on 2018/9/3.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
