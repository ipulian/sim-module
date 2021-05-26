package com.ipusoft.sim.http;

import com.ipusoft.base_class.BaseHttpObserve;
import com.ipusoft.base_class.bean.Customer;
import com.ipusoft.base_class.constant.HttpStatus;
import com.ipusoft.base_communication.NativeInterface;
import com.ipusoft.component.dialog.IAlertDialog;
import com.ipusoft.context.IActivityLifecycle;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.network.RequestMap;
import com.ipusoft.sim.bean.SimRiskControl;
import com.ipusoft.sim.module.SIMService;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * author : GWFan
 * time   : 5/14/21 10:18 AM
 * desc   :
 */

public class SimHttp {
    private static final String TAG = "SimHttpRequest";
    /*
     * 0直接外呼  1无法外呼提示msg  2提示msg，提供继续外呼和取消外呼
     */
    private static final int TYPE_0 = 0;
    private static final int TYPE_1 = 1;
    private static final int TYPE_2 = 2;

    public interface OnSimCallInfoListener {
        void onSimCallInfo(String type, Customer customer);
    }

    /**
     * 主卡外呼风控
     */
    public static void querySimRiskControl(String phone, BaseHttpObserve<SimRiskControl> observer) {
        RequestMap requestMap = RequestMap.getRequestMap();
        requestMap.put("phone", phone);
        SIMService.Companion.simCallPhone(requestMap, observer);
    }

    public static void querySimRiskControl(String phone) {
        RequestMap requestMap = RequestMap.getRequestMap();
        requestMap.put("phone", phone);
        SIMService.Companion.simCallPhone(requestMap, new BaseHttpObserve<SimRiskControl>() {
            @Override
            public void onNext(@NonNull SimRiskControl simRiskControl) {
                String status = simRiskControl.getStatus();
                if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                    int type = simRiskControl.getType();
                    String msg = simRiskControl.getMsg();
                    if (TYPE_1 == type || TYPE_2 == type) {
                        //showRiskControlDialog(type, msg, phone);
                    } else {
                        NativeInterface.callOutBySim(phone);
                    }
                } else if (StringUtils.equals(HttpStatus.EXPIRED, status)) {

                } else {
                    NativeInterface.callOutBySim(phone);
                }
            }
        });
    }

    public static void querySimRiskControl(String phone, OnSimCallInfoListener listener) {
        RequestMap requestMap = RequestMap.getRequestMap();
        requestMap.put("phone", phone);
        SIMService.Companion.simCallPhone(requestMap, new BaseHttpObserve<SimRiskControl>() {
            @Override
            public void onNext(@NonNull SimRiskControl simRiskControl) {
                String status = simRiskControl.getStatus();
                if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                    int type = simRiskControl.getType();
                    String msg = simRiskControl.getMsg();
                    String isClue = simRiskControl.getIsClue();
                    Customer customer = simRiskControl.getCustomer();
                    if (TYPE_1 == type || TYPE_2 == type) {
                        showRiskControlDialog(type, msg, phone, isClue, customer, listener);
                    } else {
                        if (listener != null) {
                            listener.onSimCallInfo(isClue, customer);
                        }
                    }
                }
            }
        });
    }


    public static void showRiskControlDialog(int type, String msg, String phone,
                                             String isClue, Customer customer,
                                             OnSimCallInfoListener listener) {
        IAlertDialog.getInstance(IActivityLifecycle.getCurrentActivity())
                .setMsg(msg)
                .setShowCancelBtn(type == 2)
                .setConfirmText(type == 1 ? "好的" : "")
                .setCancelableClickOutSide(false)
                .setMyAlertClickListener(new IAlertDialog.OnMyAlertClickListener() {
                    @Override
                    public void onConfirm() {
                        if (type == 2) {
                            if (listener != null) {
                                listener.onSimCallInfo(isClue, customer);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }
}
