package com.ipusoft.sim.utils;


import com.ipusoft.context.utils.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : GWFan
 * time   : 7/1/20 7:13 PM
 * desc   : 电话号码相关工具类
 */

public class PhoneNumberUtils {
    private static final String TAG = "PhoneNumberUtils";

    /**
     * 提取字符串中的电话号码
     *
     * @param num
     * @return
     */
    public static String[] getPhoneFormString(String num) {
        if (StringUtils.isEmpty(num)) {
            return null;
        }
        StringBuilder bf = new StringBuilder();
        Pattern pattern = Pattern.compile("((1[3-9])\\d{9})|((0[1-9])\\d{7,9})|((0[1-9][0-9]-)\\d{7,9})|((0[1-9][0-9][0-9]-)\\d{7,9})");
        Matcher matcher = pattern.matcher(num);
        while (matcher.find()) {
            bf.append(matcher.group()).append(",");
        }
        int len = bf.length();
        if (len > 0) {
            bf.deleteCharAt(len - 1);
        }
        String s = bf.toString();
        String[] result = null;
        if (StringUtils.isNotEmpty(s)) {
            result = s.split(",");
        }
        return result;
    }
}
