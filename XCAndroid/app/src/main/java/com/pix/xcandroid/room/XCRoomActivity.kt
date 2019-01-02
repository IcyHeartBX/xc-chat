package com.pix.xcandroid.room

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pix.xcandroid.R
import com.pix.xcandroid.bean.ChatUserInfo
import com.pix.xcserverlibrary.utils.LogUtils
import kotlinx.android.synthetic.main.activity_xcroom.*

class XCRoomActivity : AppCompatActivity(),IXCRoomView, View.OnClickListener,RoomInputView.OnSendMsgListener {
    val TAG:String = "XCRoomActivity"
    var presenter:XCRoomPresenter = XCRoomPresenter()
    var userName :String="未命名"

    companion object {
        const val EXT_USER_NAME = "nickname"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xcroom)
        if(null != intent) {
            userName = intent.getStringExtra(EXT_USER_NAME)
        }
        LogUtils.d(TAG,"nickname:" + userName)
        presenter.userName = userName
        presenter.setView(this)
        rl_keyboard.setOnClickListener(this)
        room_input_view.setOnSendMsgListener(this)
        presenter.connectServer()
    }

    override fun showChatMsg(sender: ChatUserInfo, content: String) {
        layout_message_panel.showChatMsg(sender,content,1)
    }

    override fun onClick(v: View?) {
        when(v) {
            rl_keyboard -> {
                room_input_view.setSoftKeyboardVisible(true)
            }
        }
    }
    override fun sendMsg(msg: String?) {
        presenter.sendMsg(msg!!)
    }

    override fun showServerState(state: String) {
        layout_message_panel.showSystemMsg(state)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.exitServer()
        if(null != layout_message_panel) {
            layout_message_panel.destroy()
        }
    }
}
