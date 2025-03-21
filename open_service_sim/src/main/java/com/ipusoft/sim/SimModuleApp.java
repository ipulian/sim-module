package com.ipusoft.sim;

import android.content.Intent;

import com.elvishew.xlog.XLog;
import com.ipusoft.context.AppContext;
import com.ipusoft.context.AppManager;
import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.ServiceManager;
import com.ipusoft.localcall.datastore.SimDataRepo;
import com.ipusoft.localcall.service.AppCoreService;
import com.ipusoft.sim.ifaceimpl.OnPhoneStateChangedListenerImpl;
import com.ipusoft.utils.StringUtils;

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
        boolean serviceRunning = ServiceManager.isServiceRunning(AppCoreService.class);
        if (runningForeground && !serviceRunning) {
            String token = AppContext.getToken();
            if (token.isEmpty()) {
                XLog.e(TAG + "->initModule->Token is empty!");
            }
            if (StringUtils.isNotEmpty(token)) {
                AppContext.getAppContext().startService(new Intent(AppContext.getAppContext(), AppCoreService.class));
            }
        }
    }

    @Override
    public void unInitModule() {
        SimDataRepo.clearAllData();
    }
}
