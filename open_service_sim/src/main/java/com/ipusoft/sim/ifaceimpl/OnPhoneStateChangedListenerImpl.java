package com.ipusoft.sim.ifaceimpl;

import android.util.Log;

import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.listener.OnPhoneStateChangedListener;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.bean.SIMCallOutBean;
import com.ipusoft.sim.cache.AppCache;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.manager.CallLogManager;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * author : GWFan
 * time   : 5/30/21 11:35 AM
 * desc   :
 */

public class OnPhoneStateChangedListenerImpl implements OnPhoneStateChangedListener {
    private static final String TAG = "OnPhoStateChaListenerI";

    @Override
    public void onDialingListener() {
        String outCallNumber = AppCache.getOutCallNumber();
        if (StringUtils.isNotEmpty(outCallNumber)) {
            long l = System.currentTimeMillis();
            SimDataRepo.addSIMCallOutBean(new SIMCallOutBean(outCallNumber, l));
            AppCache.setSIMCallOutCallId(l);
            AppCache.setOutCallNumber("");
        }
    }

    @Override
    public void onInComingListener() {

    }

    @Override
    public void onConnectedListener() {

    }

    @Override
    public void onDisConnectedListener() {
        Log.d(TAG, "onDialingListener: ------------->onDisConnected");
        CallLogManager.getInstance().queryCallLogAndRecording(new IObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {

            }
        });
    }
}
