package com.ipusoft.sim.upload;

import android.util.Log;

import com.ipusoft.context.IpuSoftSDK;
import com.ipusoft.context.bean.SysRecording;
import com.ipusoft.context.utils.StringUtils;
import com.ipusoft.sim.base.UploadFileObserve;
import com.ipusoft.sim.bean.UploadResponse;
import com.ipusoft.sim.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;


/**
 * author : GWFan
 * time   : 4/9/21 12:04 PM
 * desc   :
 */

public class MultipartBuilder {
    private static final String TAG = "MultipartBuilder";

    /**
     * 单文件上传
     *
     * @param sysRecording
     * @return fileUploadObserver
     */
    public static MultipartBody file2MultipartBody(SysRecording sysRecording, UploadFileObserve<UploadResponse> fileUploadObserver) {
        ArrayList<SysRecording> list = new ArrayList<>();
        if (sysRecording != null) {
            list.add(sysRecording);
        }
        return files2MultipartBody(list, fileUploadObserver);
    }

    /**
     * 多文件上传
     *
     * @param recordingFiles     文件列表
     * @param fileUploadObserver 文件上传回调
     */
    public static MultipartBody files2MultipartBody(List<SysRecording> recordingFiles,
                                                    UploadFileObserve<UploadResponse> fileUploadObserver) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        UploadFileRequestBody uploadFileRequestBody;
        for (SysRecording recording : recordingFiles) {
            String absolutePath = recording.getAbsolutePath();
            if (StringUtils.isNotEmpty(absolutePath)) {
                uploadFileRequestBody = new UploadFileRequestBody(recording, fileUploadObserver);
                builder.addFormDataPart("record", recording.getFileName(), uploadFileRequestBody);
                builder.addFormDataPart("fileMD5", recording.getFileMD5());
            }
            int callType = recording.getCallType();
            builder.addFormDataPart("callId", recording.getCallId() + "");
            builder.addFormDataPart("name", StringUtils.null2Empty(recording.getPhoneName()));
            builder.addFormDataPart("phone", recording.getPhoneNumber());
            builder.addFormDataPart("startTime", DateTimeUtils.millis2String(recording.getCallTime()));
            builder.addFormDataPart("duration", recording.getDuration() + "");
            builder.addFormDataPart("callResult", recording.getCallResult() + "");
            builder.addFormDataPart("callType", callType == 1 ? "2" : callType == 2 ? "1" : callType + "");
            builder.addFormDataPart("token", IpuSoftSDK.getToken());
            Log.d(TAG, "filesToMultipartBody: ------" + IpuSoftSDK.getToken());
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }
}

