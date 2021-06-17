package com.ipusoft.sim;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.ifaceimpl.OnPhoneStateChangedListenerImpl;

/**
 * author : GWFan
 * time   : 5/30/21 11:32 AM
 * desc   :
 */

public class SimModuleApp extends IpuSoftSDK {
    @Override
    public void initModule() {
        /**
         * 注册通话状态的listener
         */
        IpuSoftSDK.registerPhoneStatusChangedListener(new OnPhoneStateChangedListenerImpl());
    }

    @Override
    public void unInitModule() {
        SimDataRepo.clearAllData();
    }
}
