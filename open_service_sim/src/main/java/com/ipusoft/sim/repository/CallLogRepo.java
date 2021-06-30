package com.ipusoft.sim.repository;

import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.constant.CallTypeConfig;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.PlatformUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.mmkv.datastore.CommonDataRepo;
import com.ipusoft.sim.bean.SIMCallOutBean;
import com.ipusoft.sim.bean.SysCallLog;
import com.ipusoft.sim.bean.UploadSysRecordingBean;
import com.ipusoft.sim.constant.CallLogCallsType;
import com.ipusoft.sim.constant.CallLogType;
import com.ipusoft.sim.datastore.SimDataRepo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * author : GWFan
 * time   : 4/29/21 9:13 AM
 * desc   : 系统通话记录
 */

public class CallLogRepo {
    private static final String TAG = "CallLogRepository";

    private static class CallLogRepoHolder {
        private static final CallLogRepo INSTANCE = new CallLogRepo();
    }

    public static CallLogRepo getInstance() {
        return CallLogRepoHolder.INSTANCE;
    }


    public void querySysCallLog(Observer<List<SysCallLog>> observer) {
        Observable.create((ObservableOnSubscribe<List<SysCallLog>>) emitter
                -> {
            Thread.sleep(5000);
            emitter.onNext(querySysCallLog());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public List<SysCallLog> querySysCallLog() {
        ArrayList<SysCallLog> list = new ArrayList<>();
        String localCallType = CommonDataRepo.getLocalCallType();
        UploadSysRecordingBean uploadSysCallLog = SimDataRepo.getUploadSysCallLog();
        Log.d(TAG, "querySysCallLog: ------->" + localCallType + "---->" + uploadSysCallLog.isFlag());
        if (StringUtils.equals(CallTypeConfig.SIM.getType(), localCallType) && uploadSysCallLog.isFlag()) {
            Log.d(TAG, "querySysCallLog: ------>开始查数据");
            String selectionClause = CallLog.Calls.DATE + " > ? ";
            String[] selectionArgs = {
                    Math.max(uploadSysCallLog.getTimestamp(),
                            System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000) + ""};
            Cursor cursor = IpuSoftSDK.getAppContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, selectionClause, selectionArgs, CallLog.Calls.DEFAULT_SORT_ORDER);
            List<SysCallLog> sysCallLogs = getDataFormCursor(cursor);
            long maxTime = uploadSysCallLog.getTimestamp();
            if (ArrayUtils.isNotEmpty(sysCallLogs)) {
//                Log.d(TAG, "querySysCallLog1: ----->" + GsonUtils.toJson(sysCallLogs));
                List<SIMCallOutBean> simCallOutBeanList = SimDataRepo.getSIMCallOutBean();
                long beginTime;
                for (SysCallLog callLog : sysCallLogs) {
                    beginTime = callLog.getBeginTime();
                    if (beginTime > maxTime) {
                        maxTime = beginTime;
                    }
                    if (callLog.getCallType() == CallLogType.OUTGOING_TYPE.getType()) {
                        if (ArrayUtils.isNotEmpty(simCallOutBeanList)) {
                            SIMCallOutBean bean;
                            for (int i = simCallOutBeanList.size() - 1; i >= 0; i--) {
                                bean = simCallOutBeanList.get(i);
                                int timeOffset = 3 * 1000;
                                if (PlatformUtils.isHUAWEI()) {
                                    if (callLog.getDuration() != 0) {
                                        timeOffset = 60 * 1000;
                                    }
                                }
                                if (StringUtils.equals(callLog.getPhoneNumber(), bean.getPhone()) &&
                                        Math.abs(beginTime - bean.getTimestamp()) <= timeOffset) {
                                    simCallOutBeanList.remove(i);
                                    callLog.setCallId(bean.getTimestamp());
                                    list.add(callLog);
                                }
                            }
                            SimDataRepo.setSIMCallOutBean(simCallOutBeanList);
                        }
                    } else if (callLog.getCallType() == CallLogType.INCOMING_TYPE.getType() ||
                            callLog.getCallType() == CallLogType.MISSED_TYPE.getType() ||
                            callLog.getCallType() == CallLogType.REJECTED_TYPE.getType() ||
                            callLog.getCallType() == CallLogType.BLOCKED_TYPE.getType() ||
                            callLog.getCallType() == CallLogType.VOICEMAIL_TYPE.getType()) {
//                        Log.d(TAG, "querySysCallLog2: ------->" + GsonUtils.toJson(callLog));
                        list.add(callLog);
                    }
                }
//                Log.d(TAG, "querySysCallLog: ----->" + maxTime);
                //SIMDataStore.setUploadSysCallLog(true, maxTime);
            }
        }
//        Log.d(TAG, "querySysCallLog3: ----------" + GsonUtils.toJson(list));
        return list;
    }

    public List<SysCallLog> getDataFormCursor(Cursor cursor) {
        List<SysCallLog> list = new ArrayList<>();
        SysCallLog sysCallLog;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                //联系人姓名
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                //联系人号码
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                //外呼开始时间
                long callTime = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                //通话时长
                int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                //通话类型
                int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                sysCallLog = new SysCallLog();
                sysCallLog.setName(name);
                sysCallLog.setPhoneNumber(number);
                sysCallLog.setBeginTime(callTime);
                sysCallLog.setDuration(duration);
                sysCallLog.setCallResult(0);//成功
                if (type == CallLogType.REJECTED_TYPE.getType() || type == CallLogType.VOICEMAIL_TYPE.getType()) {
                    sysCallLog.setCallResult(1);//未接
                    sysCallLog.setDuration(0);
                } else if (type == CallLogType.BLOCKED_TYPE.getType()) {
                    sysCallLog.setCallResult(2);//黑名单
                    sysCallLog.setDuration(0);
                }

                /**
                 * 时长为0，算未接通
                 */
                if (duration == 0) {
                    sysCallLog.setCallResult(1);
                }
//                Log.d(TAG, "getDataFormCursor: ---------》" + type);
                int t = CallLogCallsType.OUTGOING_TYPE.getType();//外呼
                if (type == CallLogType.INCOMING_TYPE.getType() || type == CallLogType.BLOCKED_TYPE.getType()
                        || type == CallLogType.MISSED_TYPE.getType() || type == CallLogType.VOICEMAIL_TYPE.getType()
                        || type == CallLogType.REJECTED_TYPE.getType()) {
                    t = CallLogCallsType.INCOMING_TYPE.getType();//呼入
                }
                sysCallLog.setCallType(t);
                list.add(sysCallLog);
            }
            cursor.close();
        }
        return list;
    }
}
