package com.pix.xcandroid

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import com.pix.xcandroid.manager.UserManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_custom_toolbar.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        toolbar.setLogo(R.mipmap.ic_launcher)
        toolbar.title = "欢迎来到XC聊天室"
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dl_drawer.addDrawerListener(ActionBarDrawerToggle(this,dl_drawer,toolbar,R.string.app_name, R.string.app_name))

        // 设置个人信息
        tv_uid.text = UserManager.uid
        tv_name.text = UserManager.name
        tv_email.text = UserManager.email
    }

    override fun onClick(v: View?) {
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
