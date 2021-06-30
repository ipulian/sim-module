package com.ipusoft.sim.interceptors;

import com.ipusoft.http.interceptors.base.BaseSimHttpInterceptor;

import java.util.Map;

/**
 * author : GWFan
 * time   : 5/30/21 2:49 PM
 * desc   :
 */

public class SimCallEventInterceptor extends BaseSimHttpInterceptor {

    @Override
    public void interceptor(Map<String, Object> params) {
        /*
         * 该操作移到SimPhoneManager
         */
        //String phone = (String) params.get("phone");
        //AppCacheContext.setSIMOutCallNumber(phone);
    }
}
