package com.pix.xcandroid.room

import android.content.DialogInterface
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.pix.xcandroid.R
import com.pix.xcandroid.bean.ChatUserInfo
import com.pix.xcserverlibrary.utils.LogUtils
import kotlinx.android.synthetic.main.activity_xcroom.*
import android.view.MotionEvent
import kotlinx.android.synthetic.main.li_layout_room_input_view.*


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
        room_input_view.setOnSendMsgListener(this)
        presenter.connectServer()
    }
    override fun showChatMsg(sender: ChatUserInfo, content: String) {
        layout_message_panel.showChatMsg(sender,content,1)
    }
    override fun sendMsg(msg: String?) {
        presenter.sendMsg(msg!!)
    }

    override fun showServerState(state: String) {
        layout_message_panel.showSystemMsg(state)
    }

    // 显示dialog
    override fun showAlertDialog(msg: String) {

    }

    override fun showRoomNoExist() {
       AlertDialog.Builder(this)
               .setMessage("房间不存在!")
               .setPositiveButton("确定", object:DialogInterface.OnClickListener{
                     override fun onClick(dialog: DialogInterface, which: Int) {
                         finish()
                    }
               })
               .create()
               .show()
    }

    override fun onClick(v: View?) {
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.getAction() and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> if (hideInputView(ev)) {
                return true
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //MessageView的表情和输入框消失
    protected fun hideInputView(ev: MotionEvent): Boolean {
        if (room_input_view.getVisibility() === View.VISIBLE) {
            if (!isClickInputlayout(ev.rawX.toInt(), ev.rawY.toInt())) {
                room_input_view.setSoftKeyboardVisible(false)
                return true
            }
        }
        return false
    }

    fun isClickInputlayout(rawX: Int, rawY: Int): Boolean {
        val messageViewRect = Rect()
        room_input_view.getGlobalVisibleRect(messageViewRect)
        return if (messageViewRect.contains(rawX, rawY)) {
            true
        } else false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.exitServer()
        if(null != layout_message_panel) {
            layout_message_panel.destroy()
        }
    }
}
