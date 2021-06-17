package com.ipusoft.sim.ifaceimpl;

import android.util.Log;

import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.listener.OnPhoneStateChangedListener;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.bean.SIMCallOutBean;
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
        String simOutCallNumber = AppCacheContext.getSIMOutCallNumber();
        if (StringUtils.isNotEmpty(simOutCallNumber)) {
            long l = System.currentTimeMillis();

            SimDataRepo.addSIMCallOutBean(new SIMCallOutBean(simOutCallNumber, l));

            AppCacheContext.setSIMCallOutCallId(l);
            AppCacheContext.setSIMOutCallNumber("");
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
