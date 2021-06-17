package com.ipusoft.sim.debug;

import android.app.Application;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.bean.AuthInfo;
import com.ipusoft.context.config.IEnv;

/**
 * author : GWFan
 * time   : 5/28/21 9:58 AM
 * desc   :
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String key = "4571122846924808";
        String secret = "90237f5970f805250f07fef18fff45cb";
        String username = "17047151254";
        IpuSoftSDK.init(this, IEnv.DEV, new AuthInfo(key, secret, username));
    }
}
