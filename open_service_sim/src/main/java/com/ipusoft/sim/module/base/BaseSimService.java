package com.ipusoft.sim.module.base;

import com.ipusoft.http.interceptors.base.BaseSimHttpInterceptor;
import com.ipusoft.sim.iface.OnSimCallPhoneResultListener;
import com.ipusoft.sim.interceptors.SimCallEventInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author : GWFan
 * time   : 5/30/21 4:45 PM
 * desc   :
 */

public abstract class BaseSimService {

    private static List<BaseSimHttpInterceptor> interceptors;

    public abstract <T> void simCallPhone(Map<String, Object> params, Class<T> clazz,
                                          OnSimCallPhoneResultListener<T> listener);

    /**
     * 初始化默认拦截器
     */
    public static void initInterceptors() {
        interceptors = new ArrayList<>();
        interceptors.add(new SimCallEventInterceptor());
    }

    /**
     * 返回所有拦截器
     *
     * @return
     */
    public List<BaseSimHttpInterceptor> getInterceptors() {
        return interceptors;
    }

    /**
     * 添加自定义拦截器
     *
     * @param interceptor
     * @return
     */
    public BaseSimService addInterceptor(BaseSimHttpInterceptor interceptor) {
        interceptors.add(interceptor);
        return this;
    }
}
