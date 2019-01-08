package com.pix.xcandroid.room

import com.pix.xcandroid.XCManager
import com.pix.xcandroid.bean.ChatUserInfo

class XCRoomPresenter : RoomServerListener{
    var xcManager = XCManager.instance

//    val IP:String = "192.168.26.120"
    val IP:String = "192.168.1.109"
//    val IP:String = "96.45.176.19"
    val PORT:Int = 7777
    var roomView:IXCRoomView? = null
    var userName:String="未命名"
    init {
        xcManager.setServerAddress(IP,PORT)
        xcManager.setRoomServerListener(this)
    }

    fun setView(view:IXCRoomView) {
        roomView = view
    }
    /**
     * 连接服务器
     */
    fun connectServer() {
        xcManager.connect(7777, userName,8888,"token")
    }

    /**
     * 发送聊天消息
     */
    fun sendMsg(msg:String) {
        xcManager.sendChatMsg(777,userName,"img",msg)
    }

    override fun onServerStateInfo(info: String) {
        roomView?.showServerState(info)
    }

    override fun onChatMsgBRO(sender: ChatUserInfo, msg: String) {
        roomView?.showChatMsg(sender,msg)
    }

    fun exitServer() {
        xcManager.disconnect()
    }
}
