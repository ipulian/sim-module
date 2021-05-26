package com.ipusoft.sim.manager;

import android.util.Log;

import com.ipusoft.base_class.BaseObserve;
import com.ipusoft.base_class.constant.Constant;
import com.ipusoft.context.manager.ThreadPoolManager;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.database.bean.SysRecording;
import com.ipusoft.sim.cache.AppCache;
import com.ipusoft.sim.constant.UploadStatus;
import com.ipusoft.sim.repository.SysRecordingRepo;
import com.ipusoft.sim.threadpool.UploadRecordingWorker;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * author : GWFan
 * time   : 4/27/21 11:08 AM
 * desc   :
 */

public class UploadManager {
    private static final String TAG = "UploadManager";

    public static void addRecordingList2Task(List<SysRecording> list) {
        if (ArrayUtils.isEmpty(list)) {
            return;
        }
        List<SysRecording> temp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            SysRecording item = list.get(i);
            long fileSize = item.getFileSize();
            if (fileSize <= Constant.MAX_FILE_SIZE && AppCache.addFile2UploadTask(item)) {
                item.setUploadStatus(UploadStatus.WAIT_UPLOAD.getStatus());
                temp.add(item);
            }
        }

        /**
         * 如果上传队列中没有当前任务，将任务加入上传队列
         */
        SysRecordingRepo.updateRecordingList(temp, new BaseObserve<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                for (SysRecording item : temp) {
                    Log.d(TAG, "onNext: ----------》" + GsonUtils.toJson(item));
                    ThreadPoolManager.newInstance().addExecuteTask(new UploadRecordingWorker(item));
                }
            }
        });
    }

    public static void addRecording2Task(SysRecording sysRecording) {
        if (sysRecording == null) {
            return;
        }
        long fileSize = sysRecording.getFileSize();
        if (fileSize > Constant.MAX_FILE_SIZE) {
            return;
        }
        /**
         * 如果上传队列中没有当前任务，将任务加入上传队列
         */
        if (AppCache.addFile2UploadTask(sysRecording)) {
            Log.d(TAG, "addRecording2Task: ------->" + GsonUtils.toJson(sysRecording));
            SysRecordingRepo.updateRecordingStatusByKey(sysRecording, UploadStatus.WAIT_UPLOAD.getStatus(),
                    new BaseObserve<SysRecording>() {
                        @Override
                        public void onNext(@NonNull SysRecording recording) {
                            Log.d(TAG, "addUploadFile2Task: ------->加入队列");
                            ThreadPoolManager.newInstance().addExecuteTask(new UploadRecordingWorker(recording));
                        }
                    });
        }
    }
}
