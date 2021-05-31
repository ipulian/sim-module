package com.ipusoft.sim.interceptors;

import com.ipusoft.sim.cache.AppCache;
import com.ipusoft.sim.interceptors.base.BaseSimHttpInterceptor;

import java.util.Map;

/**
 * author : GWFan
 * time   : 5/30/21 2:49 PM
 * desc   :
 */

public class SimCallEventInterceptor extends BaseSimHttpInterceptor {

    @Override
    public void interceptor(Map<String, Object> params) {
        String phone = (String) params.get("phone");
        AppCache.setOutCallNumber(phone);
    }
}
