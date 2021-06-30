package com.ipusoft.sim;

import android.content.Intent;
import android.util.Log;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.AppManager;
import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.ServiceManager;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.ifaceimpl.OnPhoneStateChangedListenerImpl;
import com.ipusoft.sim.service.WHCoreService;

/**
 * author : GWFan
 * time   : 5/30/21 11:32 AM
 * desc   :
 */

public class SimModuleApp extends IpuSoftSDK {
    private static final String TAG = "runningForeground";

    @Override
    public void initModule() {
        /**
         * 注册通话状态的listener
         */
        IpuSoftSDK.registerPhoneStatusChangedListener(new OnPhoneStateChangedListenerImpl());

        boolean runningForeground = AppManager.isRunningForeground(AppContext.getAppContext());
        Log.d(TAG, "initModule: ----->" + runningForeground);
        boolean serviceRunning = ServiceManager.isServiceRunning(WHCoreService.class);
        if (runningForeground && !serviceRunning) {
            String token = AppContext.getToken();
            if (StringUtils.isNotEmpty(token)) {
                AppContext.getAppContext().startService(new Intent(AppContext.getAppContext(), WHCoreService.class));
            }
        }
    }

    @Override
    public void unInitModule() {
        SimDataRepo.clearAllData();
    }
}
