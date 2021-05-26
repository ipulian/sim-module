package com.ipusoft.sim.bean;

import com.ipusoft.base_class.bean.base.BaseHttpResponse;
import com.ipusoft.base_class.bean.Customer;

/**
 * author : GWFan
 * time   : 5/14/21 10:00 AM
 * desc   :
 */

public class SimRiskControl extends BaseHttpResponse {
    /*
     * 0直接外呼  1无法外呼提示msg  2提示msg，提供继续外呼和取消外呼
     */
    private int type;
    /*
     * 外呼号码
     */
    private String phone;

    private Customer customer;

    private String isClue;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getIsClue() {
        return isClue;
    }

    public void setIsClue(String isClue) {
        this.isClue = isClue;
    }
}
