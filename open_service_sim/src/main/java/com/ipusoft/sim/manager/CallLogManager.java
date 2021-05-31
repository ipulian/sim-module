package com.ipusoft.sim.manager;

import android.util.Log;

import com.ipusoft.context.IActivityLifecycle;
import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.context.constant.CallTypeConfig;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.context.utils.ThreadUtils;
import com.ipusoft.sim.bean.SysCallLog;
import com.ipusoft.sim.bean.UploadSysRecordingBean;
import com.ipusoft.sim.component.CheckRecordingFileDialog;
import com.ipusoft.sim.constant.CallLogCallsType;
import com.ipusoft.sim.constant.UploadStatus;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.repository.CallLogRepo;
import com.ipusoft.sim.repository.FileRepository;
import com.ipusoft.sim.repository.RecordingFileRepo;
import com.ipusoft.sim.repository.SysRecordingRepo;
import com.ipusoft.sim.utils.FileUtilsKt;
import com.ipusoft.sim.utils.PhoneNumberUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * author : GWFan
 * time   : 4/30/21 10:20 AM
 * desc   :
 */

public class CallLogManager {
    private static final String TAG = "CallLogManager";
    private static CallLogManager instance;

    private CallLogManager() {

    }

    public static CallLogManager getInstance() {
        if (instance == null) {
            synchronized (CallLogManager.class) {
                if (instance == null) {
                    instance = new CallLogManager();
                }
            }
        }
        return instance;
    }

    /**
     * 查询通话记录合并录音
     */
    public void queryCallLogAndRecording(IObserver<Boolean> observe) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            List<SysRecording> list = new ArrayList<>();
            String localCallType = SimDataRepo.getLocalCallType();
            UploadSysRecordingBean uploadSysCallLog = SimDataRepo.getUploadSysCallLog();
            UploadSysRecordingBean uploadSysRecording = SimDataRepo.getUploadSysRecording();
            if (StringUtils.equals(CallTypeConfig.SIM.getType(), localCallType) && uploadSysCallLog.isFlag()) {
                Log.d(TAG, "-------->5s后系统数据库中查数据");
                CallLogRepo.getInstance().querySysCallLog(new IObserver<List<SysCallLog>>() {
                    @Override
                    public void onNext(@NonNull List<SysCallLog> sysCallLogs) {
                        boolean flag = false;
//                        Log.d(TAG, "onNext: -------->" + GsonUtils.toJson(sysCallLogs));
                        List<String> phoneList = new ArrayList<>();
                        long maxTimestamp = uploadSysCallLog.getTimestamp();
                        if (ArrayUtils.isNotEmpty(sysCallLogs)) {
                            for (SysCallLog callLog : sysCallLogs) {
                                if (callLog.getBeginTime() > maxTimestamp) {
                                    maxTimestamp = callLog.getBeginTime();
                                }
                                if (callLog.getDuration() != 0 ||
                                        callLog.getCallType() == CallLogCallsType.INCOMING_TYPE.getType()
                                        || callLog.getCallType() == CallLogCallsType.OUTGOING_TYPE.getType()) {
                                    flag = true;
                                }
                                phoneList.add(callLog.getPhoneNumber());
                            }
                        }
//                        Log.d(TAG, "phoneList: -------->" + GsonUtils.toJson(phoneList));
                        if (uploadSysRecording.isFlag() && flag && ArrayUtils.isNotEmpty(phoneList)) {
                            long finalMaxTimestamp = maxTimestamp;
                            RecordingFileRepo.getInstance().queryRecordingFile(new IObserver<List<File>>() {
                                @Override
                                public void onNext(@NonNull List<File> files) {
                                    Map<String, File> fileMap = new HashMap<>();// 联系人号码+时间和录音文件的map
//                                    Log.d(TAG, "onNext1: ------------>" + GsonUtils.toJson(files));
                                    for (File file : files) {
                                        if (file != null) {
                                            String fileName = StringUtils.trim(file.getName());
                                            String[] phoneFormString = PhoneNumberUtils.getPhoneFormString(fileName);
                                            if (phoneFormString != null && phoneFormString.length != 0) {
                                                for (String str : phoneFormString) {
                                                    if (phoneList.contains(str)) {
                                                        //Log.d(TAG, "onNext: --------------》" + phoneFormString[0] + "_" + file.lastModified());
                                                        fileMap.put(phoneFormString[0] + "_" + file.lastModified(), file);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Log.d(TAG, "onNext: -----=====>" + GsonUtils.toJson(fileMap));
                                    //ArrayList<SysRecording> list = new ArrayList<>();
                                    SysRecording recording;
                                    String phoneNumber;
                                    long beginTime;
                                    int duration;
                                    Map<File, File> fileWaitToCopy = new HashMap<>();
                                    boolean flag = false;
                                    for (SysCallLog callLog : sysCallLogs) {
                                        File file = null;
                                        phoneNumber = callLog.getPhoneNumber();
                                        beginTime = callLog.getBeginTime();//通话开始时间
                                        duration = callLog.getDuration();//通话时长

                                        long minDiff = Long.MAX_VALUE;
                                        for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                                            String phoneTime = entry.getKey();
                                            String[] s = phoneTime.split("_");
                                            /**
                                             * 号码匹配，时间也对应
                                             */
                                            if (StringUtils.equals(s[0], phoneNumber)) {
                                                long timeDiff = entry.getValue().lastModified() - (beginTime + duration);
                                                if (timeDiff >= 0 && timeDiff <= minDiff) {
                                                    minDiff = timeDiff;
                                                    file = entry.getValue();
                                                }
                                            }
                                        }
                                        recording = new SysRecording();
                                        recording.setDuration(callLog.getDuration());
                                        //Log.d(TAG, "onNext: ------------>" + callLog.getDuration());
                                        recording.setCallTime(callLog.getBeginTime());
                                        recording.setPhoneName(callLog.getName());
                                        recording.setPhoneNumber(phoneNumber);
                                        recording.setCallType(callLog.getCallType());
                                        recording.setCallResult(callLog.getCallResult());
                                        if (callLog.getCallId() != 0) {
                                            recording.setCallId(callLog.getCallId());
                                        } else {
                                            recording.setCallId(System.currentTimeMillis());
                                        }
                                        recording.setUploadStatus(UploadStatus.WAIT_UPLOAD.getStatus());
                                        if (file != null) {
                                            File nFile = new File(FileUtilsKt.getAudioPath(IpuSoftSDK.getAppContext()) + "/" + file.getName());
                                            recording.setAbsolutePath(nFile.getAbsolutePath());//
                                            recording.setFileName(file.getName());
                                            recording.setFileGenerateTime(file.lastModified());
                                            recording.setFileSize(file.length());
                                            recording.setFileMD5(FileUtilsKt.getFileMD5ToString(file));
                                            fileWaitToCopy.put(file, nFile);
                                        } else {
                                            Log.d(TAG, "onNextthis:------> 1");
                                            //未找到录音文件，或者用户没有打开录音功能
                                            if (!flag) {
                                                flag = true;
                                                Log.d(TAG, "onNextthis:------> 2");
                                                ThreadUtils.runOnUiThread(CallLogManager.this::showTipDialog);
                                            }
                                        }
                                        list.add(recording);
                                    }

                                    SimDataRepo.setUploadSysCallLog(true, finalMaxTimestamp);
//                                    Log.d(TAG, "onNext23: -------->" + GsonUtils.toJson(list));
                                    FileRepository.copyFileAsync(fileWaitToCopy);
                                    // Log.d(TAG, "onNext: -------->" + GsonUtils.toJson(list));
                                    /**
                                     * 入库
                                     */
                                    SysRecordingRepo.insert(list);
                                    /**
                                     * 加入任务队列
                                     */
                                    UploadManager.getInstance().addRecordingList2Task(list);

                                    emitter.onNext(true);
                                    emitter.onComplete();

                                }
                            });
                        } else {
                            if (!uploadSysRecording.isFlag()) {
                                Log.d(TAG, "onNext: --------->不上传录音");
                            }
                            if (ArrayUtils.isEmpty(sysCallLogs)) {
                                Log.d(TAG, "onNext: --------->没有通话记录");
                            }
                            if (flag) {
                                Log.d(TAG, "onNext: --------->录音都为0");
                            }

                            SysRecording recording;
                            String phoneNumber;
                            for (SysCallLog callLog : sysCallLogs) {
                                phoneNumber = callLog.getPhoneNumber();
                                recording = new SysRecording();
                                recording.setDuration(callLog.getDuration());
                                recording.setCallTime(callLog.getBeginTime());
                                recording.setPhoneName(callLog.getName());
                                recording.setPhoneNumber(phoneNumber);
                                recording.setUploadStatus(UploadStatus.WAIT_UPLOAD.getStatus());
                                recording.setCallResult(callLog.getCallResult());
                                recording.setCallType(callLog.getCallType());

                                if (callLog.getCallId() != 0) {
                                    recording.setCallId(callLog.getCallId());
                                } else {
                                    recording.setCallId(System.currentTimeMillis());
                                }

                                list.add(recording);
//                                Log.d(TAG, "onNext上传: ------->" + GsonUtils.toJson(callLog));
                            }

                            SimDataRepo.setUploadSysCallLog(true, maxTimestamp);

                            /**
                             * 入库
                             */
                            SysRecordingRepo.insert(list);
                            /**
                             * 加入任务队列
                             */
                            UploadManager.getInstance().addRecordingList2Task(list);

                            emitter.onNext(true);
                            emitter.onComplete();

                        }
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    private void showTipDialog() {
        CheckRecordingFileDialog
                .getInstance(IActivityLifecycle.getCurrentActivity())
                .show();
    }
}
