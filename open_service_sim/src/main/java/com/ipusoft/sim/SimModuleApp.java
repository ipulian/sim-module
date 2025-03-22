package com.ipusoft.sim;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
    private static final long INTERVAL = 1000; // 1秒间隔
    private Handler handler;
    private Runnable runnable;
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
//        startPeriodicTask();
    }

    @Override
    public void unInitModule() {
        SimDataRepo.clearAllData();
//        stopPeriodicTask();
    }


//    private void startPeriodicTask() {
//        handler = new Handler(Looper.getMainLooper());
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                // 获取 IpuSoftSDK.getInitStatus() 的值
//                String initStatus = IpuSoftSDK.getInitStatus();
//                if (initStatus.equals("INIT_SUCCESS")) {
//                    boolean runningForeground = AppManager.isRunningForeground(AppContext.getAppContext());
//                    boolean serviceRunning = ServiceManager.isServiceRunning(AppCoreService.class);
//                    if (runningForeground && !serviceRunning) {
//                        String token = AppContext.getToken();
//                        if (token.isEmpty()) {
//                            XLog.e(TAG + "->initModule->Token is empty!");
//                        }
//                        if (StringUtils.isNotEmpty(token)) {
//                            AppContext.getAppContext().startService(new Intent(AppContext.getAppContext(), AppCoreService.class));
//                        }
//                    }
//                    XLog.e(TAG + "startPeriodicTask: IpuSoftSDK初始化成功，结束循环");
//                    stopPeriodicTask();
//                } else {
//                    XLog.e(TAG + "startPeriodicTask: IpuSoftSDK初始化未成功，重试...");
//                }
//                // 重新安排任务
//                handler.postDelayed(this, INTERVAL);
//            }
//        };
//
//        // 启动任务
//        handler.postDelayed(runnable, INTERVAL);
//    }
//
//    private void stopPeriodicTask() {
//        if (handler != null && runnable != null) {
//            handler.removeCallbacks(runnable);
//            handler = null;
//            runnable = null;
//        }
//    }
}
