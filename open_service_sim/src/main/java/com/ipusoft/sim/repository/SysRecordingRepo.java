package com.ipusoft.sim.repository;

import android.util.Log;

import com.ipusoft.base_class.BaseObserve;
import com.ipusoft.base_class.constant.Constant;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.database.bean.SysRecording;
import com.ipusoft.database.manager.DBManager;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * author : GWFan
 * time   : 4/28/21 9:13 AM
 * desc   :
 */

public class SysRecordingRepo {
    private static final String TAG = "SysRecordingRepository";

    public static void queryByStatusForListPage(List<Integer> uploadStatus, BaseObserve<List<SysRecording>> observer) {
        DBManager.getSysRecordingDao().queryLimitRecordingByStatus(uploadStatus, Constant.PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void queryByStatus(List<Integer> uploadStatus, int retryCount, long currentTime,
                                     BaseObserve<List<SysRecording>> observe) {
        DBManager.getSysRecordingDao().queryLimitRecordingByStatus(uploadStatus, retryCount, currentTime, Constant.PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    public static void queryByStatusForMainThread(List<Integer> uploadStatus, int retryCount, long currentTime,
                                                  BaseObserve<List<SysRecording>> observe) {
        DBManager.getSysRecordingDao().queryLimitRecordingByStatus(uploadStatus, retryCount, currentTime, Constant.PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    public static void deleteRecording(SysRecording... recording) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter ->
                DBManager.getSysRecordingDao().deleteRecording(recording))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public static void deleteAllByStatus(int status) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter ->
                DBManager.getSysRecordingDao().deleteRecording(status))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public static void deleteRecording(List<Long> callTimeList) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter ->
                DBManager.getSysRecordingDao().deleteRecording(callTimeList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public static void insert(List<SysRecording> list) {
        if (ArrayUtils.isNotEmpty(list)) {
            Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                Log.d(TAG, "insert: ------->" + GsonUtils.toJson(list));
                DBManager.getSysRecordingDao().insert(list);
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    public static void updateRecordingStatusByKey(SysRecording recording, int status,
                                                  BaseObserve<SysRecording> observe) {
        Observable.create((ObservableOnSubscribe<SysRecording>) emitter -> {
            recording.setUploadStatus(status);
            DBManager.getSysRecordingDao().updateRecording(recording);
            emitter.onNext(recording);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    public static void updateRecordingStatusByKey2(SysRecording recording, int status,
                                                   BaseObserve<SysRecording> observe) {
        Observable.create((ObservableOnSubscribe<SysRecording>) emitter -> {
            recording.setUploadStatus(status);
            DBManager.getSysRecordingDao().updateRecording(recording);
            emitter.onNext(recording);
        })
                .subscribe(observe);
    }


    public static void updateRecordingStatusByKey(SysRecording recording, int status) {
        updateRecordingStatusByKey(recording, status, new BaseObserve<SysRecording>() {
            @Override
            public void onNext(@NonNull SysRecording sysRecording) {

            }
        });
    }

    public static void updateRecording(SysRecording recording, BaseObserve<Boolean> observe) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            DBManager.getSysRecordingDao().updateRecording(recording);
            emitter.onNext(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    public static void updateRecordingList(List<SysRecording> list, BaseObserve<Boolean> observe) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            DBManager.getSysRecordingDao().updateStatusList(list);
            emitter.onNext(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }

    public static void deleteOldRecording(List<Integer> statusList, long timestamp, BaseObserve<Boolean> observe) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            DBManager.getSysRecordingDao().deleteOldRecording(timestamp, statusList);
            emitter.onNext(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observe);
    }
}
