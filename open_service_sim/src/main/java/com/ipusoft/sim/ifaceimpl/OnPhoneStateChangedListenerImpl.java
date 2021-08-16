package com.ipusoft.sim.ifaceimpl;

import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.cache.AppCacheContext;
import com.ipusoft.context.listener.OnPhoneStateChangedListener;
import com.ipusoft.localcall.bean.SIMCallOutBean;
import com.ipusoft.localcall.datastore.SimDataRepo;
import com.ipusoft.localcall.manager.CallLogManager;
import com.ipusoft.logger.XLogger;

import org.jetbrains.annotations.NotNull;

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
        XLogger.d("OnPhoneStateChangedListenerImpl->Dialing");
        SIMCallOutBean simCallOutBean = AppCacheContext.getSIMCallOutBean();
        if (simCallOutBean != null) {
            long l = System.currentTimeMillis();
            SimDataRepo.addSIMCallOutBean(new SIMCallOutBean(simCallOutBean.getPhone(), simCallOutBean.getCallTime(), l));
            AppCacheContext.setSIMCallOutCallId(l);
            AppCacheContext.setSIMCallOutBean(null);
        }
    }

    @Override
    public void onInComingListener() {
        XLogger.d("OnPhoneStateChangedListenerImpl->InComing");
    }

    @Override
    public void onConnectedListener() {
        XLogger.d("OnPhoneStateChangedListenerImpl->Connected");
    }

    @Override
    public void onDisConnectedListener() {
        XLogger.d("OnPhoneStateChangedListenerImpl->DisConnected");
        CallLogManager.getInstance().queryCallLogAndRecording(new IObserver<Boolean>() {
            @Override
            public void onNext(@NotNull @NonNull Boolean aBoolean) {

            }
        });
    }
}
