package com.ipusoft.sim.module

import com.ipusoft.network.RetrofitManager
import com.ipusoft.sim.api.SimAPIService
import com.ipusoft.sim.bean.SimRiskControl
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * author : GWFan
 * time   : 5/14/21 10:04 AM
 * desc   :
 */
class SIMService {
    companion object {
        /**
         * SIM 风控
         */
        fun simCallPhone(params: MutableMap<String, Any>, observer: Observer<SimRiskControl>) {
            RetrofitManager.getInstance().retrofit.create(SimAPIService::class.java)
                    .simCallPhone(RetrofitManager.getInstance().getRequestBody(params))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)
        }
    }
}