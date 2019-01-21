package com.pix.xcandroid

import android.app.Application
import com.pix.http.OKHttpManager
import com.pix.xcandroid.http.LiveHttpConfig

class XCApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OKHttpManager.setHeaders(LiveHttpConfig().getHeader())
    }
}