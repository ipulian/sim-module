package com.ipusoft.sim.iface;

/**
 * author : GWFan
 * time   : 5/27/21 9:05 AM
 * desc   : 查询主卡风控的结果
 */

public interface OnSimCallPhoneResultListener<T> {
    void onSucceed(T t);

    void onFailure(Throwable throwable);
}
