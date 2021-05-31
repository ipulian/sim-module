package com.ipusoft.sim.manager;

import android.util.Log;

import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.base.IObserver;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.context.constant.HttpStatus;
import com.ipusoft.context.manager.ThreadPoolManager;
import com.ipusoft.context.utils.ArrayUtils;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.base.UploadFileObserve;
import com.ipusoft.sim.bean.UploadProgress;
import com.ipusoft.sim.bean.UploadResponse;
import com.ipusoft.sim.cache.AppCache;
import com.ipusoft.sim.constant.Constant;
import com.ipusoft.sim.constant.UploadStatus;
import com.ipusoft.sim.module.UploadService;
import com.ipusoft.sim.repository.SysRecordingRepo;

import java.io.File;
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

    private static UploadManager instance;

    public static UploadManager getInstance() {
        if (instance == null) {
            synchronized (UploadManager.class) {
                if (instance == null) {
                    instance = new UploadManager();
                }
            }
        }
        return instance;
    }

    public void addRecordingList2Task(List<SysRecording> list) {
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
        SysRecordingRepo.updateRecordingList(temp, new IObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                for (SysRecording item : temp) {
                    Log.d(TAG, "onNext: ----------》" + GsonUtils.toJson(item));
                    addUploadTask(item);
                }
            }
        });
    }

    public void addRecording2Task(SysRecording sysRecording) {
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
            SysRecordingRepo.updateRecordingStatusByKey(sysRecording, UploadStatus.WAIT_UPLOAD.getStatus(),
                    new IObserver<SysRecording>() {
                        @Override
                        public void onNext(@NonNull SysRecording recording) {
                            Log.d(TAG, "addUploadFile2Task: ------->加入队列");
                            addUploadTask(recording);
                        }
                    });
        }
    }

    private int i = 0;

    private void addUploadTask(SysRecording sysRecording) {
        i += 1;
        Log.d(TAG, "addUploadTask: --------" + i);
        if (sysRecording != null) {
            String absolutePath = sysRecording.getAbsolutePath();
            if (StringUtils.isNotEmpty(absolutePath)) {
                File file = new File(sysRecording.getAbsolutePath());
                if (!file.exists()) {
                    return;
                }
            }

            ThreadPoolManager.newInstance().addExecuteTask(new UploadWorker(sysRecording));
        }
    }

}

class UploadWorker implements Runnable {
    private static final String TAG = "UploadWorker";
    private SysRecording sysRecording;

    private UploadWorker() {
    }

    public UploadWorker(SysRecording sysRecording) {
        this.sysRecording = sysRecording;
    }

    @Override
    public void run() {
        //SysRecordingRepo.updateRecordingStatusByKey2(sysRecording, UploadStatus.UPLOADING.getStatus(),
        //        new IObserver<SysRecording>() {
        //            @Override
        //            public void onNext(@NonNull SysRecording recording) {
        executeUploadHttpTask(sysRecording);
        //             }
        //         });
    }

    /**
     * 执行上传任务
     *
     * @param recording
     */
    private void executeUploadHttpTask(SysRecording recording) {
        UploadFileObserve<UploadResponse> uploadFileObserve = new UploadFileObserve<UploadResponse>() {
            @Override
            public void onUploadSuccess(UploadResponse responseBody) {
                Log.d(TAG, "onUploadSuccess: --------》" + GsonUtils.toJson(responseBody));
                String type = responseBody.getType();
                String status = responseBody.getStatus();
                if (StringUtils.equals(HttpStatus.SUCCESS, status)) {
                    setUploadSucceed(type, recording);
                } else {
                    setUploadFailure(recording);
                }
            }

            @Override
            public void onUploadFail(Throwable e) {
                Log.d(TAG, "onUploadFail: --------》" + e.toString());
                setUploadFailure(recording);
            }

            @Override
            public void onProgress(int progress) {
                Log.d(TAG, "onProgress: ----" + progress);
                LiveDataBus.get().with("uploadProgress", UploadProgress.class).postValue(new UploadProgress(recording, progress));
            }
        };
        Log.d(TAG, "executeUploadHttpTask: -------->zzzzz");
        UploadService.Companion.uploadRecordingFile(recording, uploadFileObserve);
    }

    /**
     * 上传成功
     */
    private void setUploadSucceed(String type, SysRecording recording) {
        if (StringUtils.equals("1", type)) {
            SysRecordingRepo.deleteRecording(recording);
        } else {
            SysRecordingRepo.updateRecordingStatusByKey(recording,
                    UploadStatus.UPLOAD_SUCCEED.getStatus(),
                    new IObserver<SysRecording>() {
                        @Override
                        public void onNext(@NonNull SysRecording sysRecording) {
                            AppCache.removeTaskFromQueue(sysRecording);
                        }
                    });
        }
    }

    /**
     * 上传失败
     *
     * @param recording
     */
    private void setUploadFailure(SysRecording recording) {
        recording.setRetryCount(recording.getRetryCount() + 1);
        recording.setLastRetryTime(System.currentTimeMillis());
        recording.setUploadStatus(UploadStatus.UPLOAD_FAILED.getStatus());
        SysRecordingRepo.updateRecording(recording, new IObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                AppCache.removeTaskFromQueue(recording);
            }
        });
    }
}
