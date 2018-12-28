package com.pix.xcserverlibrary.utils;

import android.util.Log;

public class LogUtils {
    public static boolean ISDEBUG = true;
    public static void d(String tag,String log) {
        Log.d(tag,log);
    }
    public static void w(String tag,String log) {
        Log.w(tag,log);
    }
    public static void e(String tag,String log,Throwable t) {
        Log.e(tag,log,t);
    }
}
