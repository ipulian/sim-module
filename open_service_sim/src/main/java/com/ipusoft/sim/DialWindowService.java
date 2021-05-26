package com.ipusoft.sim;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * author : GWFan
 * time   : 5/4/21 1:14 PM
 * desc   :
 */

public class DialWindowService extends Service {
    public static final String ACTION_SHOW = "action_SHOW";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }
}
