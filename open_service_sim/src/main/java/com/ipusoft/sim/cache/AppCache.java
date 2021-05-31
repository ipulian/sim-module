package com.ipusoft.sim.cache;


import com.ipusoft.context.bean.SysRecording;

import java.util.ArrayList;
import java.util.List;

/**
 * author : GWFan
 * time   : 4/25/21 5:24 PM
 * desc   :
 */

public class AppCache {
    private static final String TAG = "AppCache";
    private static final Object object = new Object();

    private static String Token = "";

    public static String getToken() {
        return Token;
    }

    public static void setToken(String Token) {
        AppCache.Token = Token;
    }

    /**
     * 正在上传和等待上传的任务队列(通话的时间戳作为唯一性标识)
     */
    private static final List<Long> uploadQueue = new ArrayList<>();

    private static String outCallNumber;

    /**
     * 主卡外呼的callId
     */
    private static long SIMCallOutCallId;

    //private static

    /**
     * 将记录加入去重集合
     *
     * @param sysRecording
     * @return
     */
    public static boolean addFile2UploadTask(SysRecording sysRecording) {
        if (sysRecording == null) {
            return false;
        }
        long callTime = sysRecording.getCallTime();
        if (uploadQueue.contains(callTime)) {
            return false;
        }
        return uploadQueue.add(callTime);
    }

    /**
     * 从队列中移除任务
     *
     * @param sysRecording
     * @return
     */
    public static void removeTaskFromQueue(SysRecording sysRecording) {
        uploadQueue.remove(sysRecording.getCallTime());
    }

    public static String getOutCallNumber() {
        return outCallNumber;
    }

    public static void setOutCallNumber(String outCallNumber) {
        AppCache.outCallNumber = outCallNumber;
    }

    public static void setSIMCallOutCallId(long simCallOutCallId) {
        AppCache.SIMCallOutCallId = simCallOutCallId;
    }

    public static long getSIMCallOutCallId() {
        return SIMCallOutCallId;
    }
}
