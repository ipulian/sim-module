package com.ipusoft.sim.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ipusoft.context.AppContext;
import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.constant.UploadStatus;
import com.ipusoft.sim.datastore.SimDataRepo;
import com.ipusoft.sim.manager.CallLogManager;
import com.ipusoft.sim.manager.UploadManager;
import com.ipusoft.sim.repository.SysRecordingRepo;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * author : GWFan
 * time   : 4/1/21 2:52 PM
 * desc   : 上传的Service
 */

public class WHCoreService extends Service {
    private static final String TAG = "WHCoreService";

    private static final int CHECK_PERIOD = 5 * 60 * 1000;

    private static final int CHECK_EXPIRE_PERIOD = 24 * 60 * 60 * 1000;

    private static final long EXPIRE_TIME = 3 * 24 * 60 * 60 * 1000;

    private Timer mTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String token = AppContext.getToken();
        if (StringUtils.isNotEmpty(token)) {

            checkMoreRecording();

            new Handler().postDelayed(() -> {
                //每天执行一次，清除3天前的已经上传成功和忽略的数据
                long lastClearOutOfDateRecordingTime = SimDataRepo.getLastClearOutOfDateRecordingTime();
                long l = System.currentTimeMillis();
                Log.d(TAG, "onCreate: -----------------:" + (l - lastClearOutOfDateRecordingTime));
                if (l - lastClearOutOfDateRecordingTime > CHECK_EXPIRE_PERIOD) {
                    Log.d(TAG, "onCreate: -----------");
                    SimDataRepo.setLastClearOutOfDateRecordingTime(l);
                    SysRecordingRepo.deleteOldRecording(
                            ArrayUtils.createList(
                                    UploadStatus.UPLOAD_SUCCEED.getStatus(),
                                    UploadStatus.UPLOAD_IGNORE.getStatus()
                            ),
                            System.currentTimeMillis() - EXPIRE_TIME, new IObserver<Boolean>() {
                                @Override
                                public void onNext(@NonNull Boolean aBoolean) {

                                }
                            });
                }
            }, 10 * 1000);
        }
    }

    /**
     * 检查数据库中是否有更多记录
     */
    private void checkMoreRecording() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new CheckMoreDataTask(), 5 * 1000, CHECK_PERIOD);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定期检查系统中是否有更多待处理的数据
     */
    private static class CheckMoreDataTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "run: --------");
            CallLogManager.getInstance().queryCallLogAndRecording(new IObserver<Boolean>() {
                @Override
                public void onNext(@NonNull Boolean aBoolean) {
                    /*
                     * 检查数据库中是否有更多数据待上传
                     */
                    SysRecordingRepo.queryByStatus(
                            ArrayUtils.createList(
                                    UploadStatus.WAIT_UPLOAD.getStatus(),
                                    UploadStatus.UPLOADING.getStatus(),
                                    UploadStatus.UPLOAD_FAILED.getStatus()),
                            3,
                            System.currentTimeMillis(),
                            new IObserver<List<SysRecording>>() {
                                @Override
                                public void onNext(@NonNull List<SysRecording> list) {
                                    if (ArrayUtils.isNotEmpty(list)) {
                                        Log.d(TAG, "run: --需要上传的任务--" + list.size());
                                        UploadManager.getInstance().addRecordingList2Task(list);
                                    } else {
                                        Log.d(TAG, "run: ---没有需要上传的任务");
                                    }
                                }
                            });
                }
            });
        }
    }
}