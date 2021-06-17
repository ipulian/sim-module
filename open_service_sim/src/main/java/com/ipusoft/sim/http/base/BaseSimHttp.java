package com.ipusoft.sim.http.base;

import com.ipusoft.context.bean.SimRiskControlBean;
import com.ipusoft.sim.iface.OnSimCallPhoneResultListener;

/**
 * author : GWFan
 * time   : 5/30/21 5:14 PM
 * desc   :
 */

public abstract class BaseSimHttp {

    public abstract void callPhoneBySim(String phone);

    public abstract <T extends SimRiskControlBean> void callPhoneBySim(String phone, OnSimCallPhoneResultListener<T> listener);
}
