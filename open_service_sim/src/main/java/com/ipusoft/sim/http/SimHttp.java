package com.ipusoft.sim.http;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.bean.SimRiskControlBean;
import com.ipusoft.context.component.ToastUtils;
import com.ipusoft.context.constant.DateTimePattern;
import com.ipusoft.context.manager.PhoneManager;
import com.ipusoft.http.RequestMap;
import com.ipusoft.permission.RxPermissionUtils;
import com.ipusoft.sim.component.IAlertDialog;
import com.ipusoft.sim.iface.OnSimCallPhoneResultListener;
import com.ipusoft.sim.iface.SimConstant;
import com.ipusoft.sim.module.SimService;
import com.ipusoft.utils.DateTimeUtils;

/**
 * author : GWFan
 * time   : 5/14/21 10:18 AM
 * desc   :
 */

public class SimHttp {
    private static final String TAG = "SimHttp";

    private SimHttp() {
    }

    private static class SimHttpHolder {
        private static final SimHttp INSTANCE = new SimHttp();
    }

    public static SimHttp getInstance() {
        return SimHttpHolder.INSTANCE;
    }

    /**
     * 主卡外呼风控查询，由调用者处理查询结果
     */
    public void callPhoneBySim(String phone, OnSimCallPhoneResultListener<SimRiskControlBean> listener) {
        RequestMap requestMap = RequestMap.getRequestMap();
        requestMap.put("phone", phone);
        SimService.getInstance()
                .simCallPhone(requestMap, SimRiskControlBean.class, listener);
    }

    /**
     * SDK自动处理查询结果：受风控，Dialog提示，否则，直接外呼
     *
     * @param phone
     */
    public void callPhoneBySim(String phone) {
        RequestMap requestMap = RequestMap.getRequestMap();
        requestMap.put("phone", phone);
        SimService.getInstance()
                .simCallPhone(requestMap, SimRiskControlBean.class, new OnSimCallPhoneResultListener<SimRiskControlBean>() {
                    @Override
                    public void onSucceed(SimRiskControlBean simRiskControlBean) {
                        int type = simRiskControlBean.getType();
                        if (SimConstant.TYPE_1 == type || SimConstant.TYPE_2 == type) {
                            showRiskControlDialog(simRiskControlBean);
                        } else {
                            PhoneManager.callPhoneBySim(phone, simRiskControlBean.getCallTime());
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        ToastUtils.showMessage("查询出错");
                    }
                });
    }

    /**
     * 风控提示的Dialog
     *
     * @param simRiskControlBean
     */
    private static void showRiskControlDialog(SimRiskControlBean simRiskControlBean) {
        int type = simRiskControlBean.getType();
        String msg = simRiskControlBean.getMsg();
        String phone = simRiskControlBean.getPhone();
        IAlertDialog.getInstance(AppContext.getActivityContext())
                .setMsg(msg)
                .setShowCancelBtn(false)
                .setConfirmText("好的")
                .setOnConfirmClickListener(() -> {
                    //if (type == 2) {
                    //    PhoneManager.callPhoneBySim(phone, simRiskControlBean.getCallTime());
                    //}
                }).show();
    }
}
