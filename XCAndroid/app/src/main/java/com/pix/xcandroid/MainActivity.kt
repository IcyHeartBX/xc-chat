package com.pix.xcandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    var xcManager = XCManager.getInstance()
    val IP:String = "192.168.1.109"
    val PORT:Int = 7777
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        xcManager.setServerAddress(IP,PORT)
        xcManager.connect(7777,"zhangsan",8888,"token")
    }
}
