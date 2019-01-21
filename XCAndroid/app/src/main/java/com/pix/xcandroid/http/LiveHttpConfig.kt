package com.pix.xcandroid.http

import android.os.UserManager
import kotlin.collections.HashMap

class LiveHttpConfig : HashMap<String, String>() {
    private val REFERER = "http://tiantian.qq.com"

     fun getHeader(): HashMap<String, String> {
        return hashMapOf<String, String>().apply {
            var MOBILE = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL// 操作系统版本
            if (MOBILE.length > 20) { //服务器数据库表字段设置最多20个字符
                MOBILE = MOBILE.substring(0, 20)
            }
            val SV = android.os.Build.VERSION.RELEASE// 操作系统版本
            put("mobile", MOBILE)// 设备型号
            put("Referer", REFERER)
            put("signtype", 2.toString() + "")
            val timestamp = System.currentTimeMillis()
            put("ts", timestamp.toString() + "")
            put("cleartext", (+timestamp).toString() + "") //明文校验
        }
    }

}