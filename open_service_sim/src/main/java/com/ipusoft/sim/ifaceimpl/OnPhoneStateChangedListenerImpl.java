package com.ipusoft.sim.ifaceimpl;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.constant.DateTimePattern;
import com.ipusoft.context.listener.OnPhoneStateChangedListener;
import com.ipusoft.localcall.bean.SIMCallOutBean;
import com.ipusoft.localcall.datastore.SimDataRepo;
import com.ipusoft.localcall.manager.CallLogManager;
import com.ipusoft.utils.DateTimeUtils;

import java.util.List;

/**
 * author : GWFan
 * time   : 5/30/21 11:35 AM
 * desc   :
 */

public class OnPhoneStateChangedListenerImpl implements OnPhoneStateChangedListener {
    private static final String TAG = "OnPhoStateChaListenerI";
    private long timestamp = 0;

    @Override
    public void onDialingListener() {
        XLog.d("OnPhoneStateChangedListenerImpl->Dialing");
        SIMCallOutBean simCallOutBean = AppCacheContext.getSIMCallOutBean();
        if (simCallOutBean != null) {
            timestamp = System.currentTimeMillis();
            SimDataRepo.addSIMCallOutBean(new SIMCallOutBean(simCallOutBean.getPhone(), simCallOutBean.getCallTime(), timestamp));
            AppCacheContext.setSIMCallOutCallId(timestamp);
            AppCacheContext.setSIMCallOutBean(null);
        }
    }

    @Override
    public void onInComingListener() {
        XLog.d("OnPhoneStateChangedListenerImpl->InComing");
    }

    @Override
    public void onConnectedListener() {
        XLog.d("OnPhoneStateChangedListenerImpl->Connected");
    }

    @Override
    public void onDisConnectedListener() {
        XLog.d("OnPhoneStateChangedListenerImpl->DisConnected");

        //记录电话的挂断时间
        List<SIMCallOutBean> list = SimDataRepo.getSIMCallOutBean();
        for (SIMCallOutBean bean : list) {
            if (bean != null) {
                if (bean.getTimestamp() != null && bean.getTimestamp() != 0
                        && timestamp != 0 && bean.getTimestamp() == timestamp) {
                    bean.setReleaseTime(DateTimeUtils.getCurrentTime(DateTimePattern.getDateTimeWithSecondFormat()));
                    break;
                }
            }
        }
        SimDataRepo.setSIMCallOutBean(list);

        Log.d(TAG, "onDisConnectedListener: ---------------------------1");
        CallLogManager.getInstance().queryCallLogAndRecording(null);
    }
}
