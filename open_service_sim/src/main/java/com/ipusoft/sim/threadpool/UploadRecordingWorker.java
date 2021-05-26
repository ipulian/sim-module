package com.ipusoft.sim.threadpool;

import android.util.Log;

import com.ipusoft.base_class.BaseObserve;
import com.ipusoft.context.LiveDataBus;
import com.ipusoft.context.utils.GsonUtils;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.database.bean.SysRecording;
import com.ipusoft.sim.base.UploadFileObserve;
import com.ipusoft.sim.bean.UploadProgress;
import com.ipusoft.sim.bean.UploadResponse;
import com.ipusoft.sim.cache.AppCache;
import com.ipusoft.sim.constant.UploadStatus;
import com.ipusoft.sim.module.PublicHttpService;
import com.ipusoft.sim.repository.SysRecordingRepo;

import java.io.File;

import io.reactivex.rxjava3.annotations.NonNull;

/**
 * author : GWFan
 * time   : 4/20/21 11:06 AM
 * desc   : 上传本地通话记录的工作类
 */

public class UploadRecordingWorker implements Runnable {
    private static final String TAG = "UploadRecordingWorker";
    private final SysRecording sysRecording;
    private long timestamp;

    public UploadRecordingWorker(SysRecording sysRecording) {
        this.sysRecording = sysRecording;
    }

    /**
     * 执行Http上传任务
     */
    @Override
    public void run() {
        if (sysRecording == null) {
            return;
        }
        String absolutePath = sysRecording.getAbsolutePath();
        if (StringUtils.isNotEmpty(absolutePath)) {
            File file = new File(sysRecording.getAbsolutePath());
            if (!file.exists()) {
                return;
            }
        }
        SysRecordingRepo.updateRecordingStatusByKey2(sysRecording, UploadStatus.UPLOADING.getStatus(),
                new BaseObserve<SysRecording>() {
                    @Override
                    public void onNext(@NonNull SysRecording recording) {
                        UploadFileObserve<UploadResponse> uploadFileObserve = new UploadFileObserve<UploadResponse>() {
                            @Override
                            public void onUploadSuccess(UploadResponse responseBody) {
                                Log.d(TAG, "onUploadSuccess: --------》" + GsonUtils.toJson(responseBody));
                                String type = responseBody.getType();
                                if (StringUtils.equals("1", type)) {
                                    SysRecordingRepo.deleteRecording(recording);
                                } else {
                                    SysRecordingRepo.updateRecordingStatusByKey(recording,
                                            UploadStatus.UPLOAD_SUCCEED.getStatus(),
                                            new BaseObserve<SysRecording>() {
                                                @Override
                                                public void onNext(@NonNull SysRecording sysRecording) {
                                                    AppCache.removeTaskFromQueue(sysRecording);
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onUploadFail(Throwable e) {
                                Log.d(TAG, "onUploadFail: --------》" + e.toString());
                                recording.setRetryCount(recording.getRetryCount() + 1);
                                recording.setLastRetryTime(System.currentTimeMillis());
                                recording.setUploadStatus(UploadStatus.UPLOAD_FAILED.getStatus());
                                SysRecordingRepo.updateRecording(recording, new BaseObserve<Boolean>() {
                                    @Override
                                    public void onNext(@NonNull Boolean aBoolean) {
                                        AppCache.removeTaskFromQueue(recording);
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress) {
                                //long l = System.currentTimeMillis();
                                //Log.d(TAG, "onProgress: -------->" + progress);
                                //if (l - timestamp > 1200) {
                                //   timestamp = l;
                                //Log.d(TAG, "onProgress: --------->" + progress);
                                LiveDataBus.get().with("uploadProgress", UploadProgress.class).postValue(new UploadProgress(recording, progress));
                                // EventBus.getDefault().post(new EventMessage<UploadProgress>("progress", new UploadProgress(recording, progress)));
                                //}
                            }
                        };
                        //Log.d(TAG, "onNext: ----rn");
                        PublicHttpService.Companion.uploadRecordingFile(sysRecording, uploadFileObserve);
                    }
                });
    }
}
