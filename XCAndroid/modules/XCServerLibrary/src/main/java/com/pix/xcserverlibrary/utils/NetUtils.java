package com.pix.xcserverlibrary.utils;

/**
 * Created by tangpengxiang on 2018/2/5.
 */

public class NetUtils {

    public static boolean isIpMatch(String ip) {
        if(null == ip || ip.equals("")) {
            return false;
        }
        String pattern = "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)";
        if(ip.matches(pattern)) {
            return true;
        }
        return false;
    }
}
