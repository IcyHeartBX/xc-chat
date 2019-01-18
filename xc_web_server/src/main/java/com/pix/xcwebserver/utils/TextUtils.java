package com.pix.xcwebserver.utils;

public class TextUtils {
    public static boolean isEmpty(String str) {
       if(null == str || "".equals(str)) {
           return true;
       }
       return false;
    }
}
