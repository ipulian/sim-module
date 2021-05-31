package com.ipusoft.sim.bean;

import java.io.Serializable;

/**
 * author : GWFan
 * time   : 4/29/21 4:42 PM
 * desc   :
 */

public class SIMCallOutBean implements Serializable {
    private String phone;
    private Long timestamp;

    private SIMCallOutBean() {
    }

    public SIMCallOutBean(String phone, Long timestamp) {
        this.phone = phone;
        this.timestamp = timestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
