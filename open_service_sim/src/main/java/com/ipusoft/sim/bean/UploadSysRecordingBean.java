package com.ipusoft.sim.bean;

import java.io.Serializable;

/**
 * author : GWFan
 * time   : 4/28/21 5:00 PM
 * desc   :
 */

public class UploadSysRecordingBean implements Serializable {
    private boolean flag;
    private long timestamp;

    private UploadSysRecordingBean() {
    }

    public UploadSysRecordingBean(boolean flag, long timestamp) {
        this.flag = flag;
        this.timestamp = timestamp;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
