package com.ipusoft.sim.datastore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.bean.SIMCallOutBean;
import com.ipusoft.sim.bean.UploadSysRecordingBean;
import com.ipusoft.sim.constant.StorageConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * author : GWFan
 * time   : 2020/4/28 15:44
 * desc   :
 */

public class SimDataRepo {
    private static final String TAG = "LocalStorageUtils";

    /**
     * 本地通话方式
     *
     * @param str
     */
    public static void setLocalCallType(String str) {
        SIMModuleMMKV.set(StorageConstant.LOCAL_CALL_TYPE, str);
    }

    public static String getLocalCallType() {
        return SIMModuleMMKV.getString(StorageConstant.LOCAL_CALL_TYPE);
    }

    /**
     * 上传通话记录
     *
     * @param flag
     * @param timestamp
     */
    public static void setUploadSysCallLog(boolean flag, long timestamp) {
        SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_CALL_LOG, GsonUtils.toJson(new UploadSysRecordingBean(flag, timestamp)));
    }

    public static UploadSysRecordingBean getUploadSysCallLog() {
        String json = SIMModuleMMKV.getString(StorageConstant.UPLOAD_SYS_CALL_LOG);
        UploadSysRecordingBean uploadSysRecordingBean;
        if (StringUtils.isEmpty(json)) {
            uploadSysRecordingBean = new UploadSysRecordingBean(true, System.currentTimeMillis());
            SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_CALL_LOG, GsonUtils.toJson(uploadSysRecordingBean));
        } else {
            uploadSysRecordingBean = GsonUtils.fromJson(json, UploadSysRecordingBean.class);
            uploadSysRecordingBean.setFlag(true);
        }
        return uploadSysRecordingBean;
    }

    /**
     * 上传短信记录
     *
     * @param flag
     * @param timestamp
     */
    public static void setUploadSysSms(boolean flag, long timestamp) {
        SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_SMS, GsonUtils.toJson(new UploadSysRecordingBean(flag, timestamp)));
    }

    public static UploadSysRecordingBean getUploadSysSms() {
        String json = SIMModuleMMKV.getString(StorageConstant.UPLOAD_SYS_SMS);
        UploadSysRecordingBean uploadSysRecordingBean;
        if (StringUtils.isEmpty(json)) {
            uploadSysRecordingBean = new UploadSysRecordingBean(true, System.currentTimeMillis());
            SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_SMS, GsonUtils.toJson(uploadSysRecordingBean));
        } else {
            uploadSysRecordingBean = GsonUtils.fromJson(json, UploadSysRecordingBean.class);
            uploadSysRecordingBean.setFlag(true);
        }
        return uploadSysRecordingBean;
    }

    /**
     * 上传通话录音
     *
     * @param flag
     * @param timestamp
     */
    public static void setUploadSysRecording(boolean flag, long timestamp) {
        SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_RECORDING, GsonUtils.toJson(new UploadSysRecordingBean(flag, timestamp)));
    }

    public static UploadSysRecordingBean getUploadSysRecording() {
        String json = SIMModuleMMKV.getString(StorageConstant.UPLOAD_SYS_RECORDING);
        UploadSysRecordingBean uploadSysRecordingBean;
        if (StringUtils.isEmpty(json)) {
            uploadSysRecordingBean = new UploadSysRecordingBean(true, System.currentTimeMillis());
            SIMModuleMMKV.set(StorageConstant.UPLOAD_SYS_RECORDING, GsonUtils.toJson(uploadSysRecordingBean));
        } else {
            uploadSysRecordingBean = GsonUtils.fromJson(json, UploadSysRecordingBean.class);
            uploadSysRecordingBean.setFlag(true);
        }
        return uploadSysRecordingBean;
    }


    public static void setSIMCallOutBean(List<SIMCallOutBean> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        SIMModuleMMKV.set(StorageConstant.SIM_OUT_CALL_BEAN, GsonUtils.toJson(list));
    }

    public static void addSIMCallOutBean(SIMCallOutBean bean) {
        List<SIMCallOutBean> list = getSIMCallOutBean();
        list.add(bean);
        SIMModuleMMKV.set(StorageConstant.SIM_OUT_CALL_BEAN, GsonUtils.toJson(list));
    }

    public static List<SIMCallOutBean> getSIMCallOutBean() {
        String json = SIMModuleMMKV.getString(StorageConstant.SIM_OUT_CALL_BEAN);
        ArrayList<SIMCallOutBean> simCallOutBeans = new ArrayList<>();
        if (StringUtils.isNotEmpty(json)) {
            simCallOutBeans = new Gson().fromJson(json, new TypeToken<List<SIMCallOutBean>>() {
            }.getType());
        }
        return simCallOutBeans;
    }

    public static void setLastShowCheckRecordingPermissionDialog(long timestamp) {
        SIMModuleMMKV.set(StorageConstant.LAST_SHOW_RECORDING_PERMISSION_DIALOG_TIME, timestamp);
    }

    public static long getLastShowCheckRecordingPermissionDialog() {
        return SIMModuleMMKV.getLong(StorageConstant.LAST_SHOW_RECORDING_PERMISSION_DIALOG_TIME);
    }
}
