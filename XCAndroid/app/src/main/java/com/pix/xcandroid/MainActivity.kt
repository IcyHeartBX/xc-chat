package com.pix.xcandroid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.pix.xcandroid.room.XCRoomActivity
import com.pix.xcserverlibrary.utils.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.channels.InterruptedByTimeoutException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        btn_enter_room.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            btn_enter_room-> {
                if(TextUtils.isEmpty(et_name.text.toString())) {
                    Toast.makeText(this,"请输入昵称！",Toast.LENGTH_SHORT).show()
                    return
                }
                var intent = Intent(this,XCRoomActivity::class.java)
                intent.putExtra(XCRoomActivity.EXT_USER_NAME,et_name.text.toString())
                startActivity(intent)
                et_name.setText("")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
