package com.pix.xcandroid

import com.pix.xcandroid.bean.ChatUserInfo
import com.pix.xcandroid.room.RoomServerListener
import com.pix.xcserverlibrary.IXCServerService
import com.pix.xcserverlibrary.XCServerListener
import com.pix.xcserverlibrary.XCServerManager

class XCManager private constructor() : XCServerListener {
    private val serverService: IXCServerService?

    private var uid: Long = 0

    private var uname: String? = null
    private var rid: Long = 0
    private var token: String? = null
    private var roomServerListener: RoomServerListener? = null
    val ip: String?
        get() = serverService?.ip
    val port: Int
        get() = serverService?.port ?: -1

    init {
        serverService = XCServerManager.getInstance()
        serverService!!.setXCServerListener(this)
    }

    /**
     * 服务器地址
     *
     * @param ip
     * @param port
     */
    fun setServerAddress(ip: String, port: Int) {
        serverService?.setServerAddres(ip, port)
    }

    /**
     * 连接服务器
     *
     * @param userId 用户id
     */
    fun connect(userId: Long, userName: String, roomId: Long, token: String) {
        this.uid = userId
        this.uname = userName
        this.rid = roomId
        this.token = token
        serverService?.loginServer(userId, userName, roomId, token)
    }

    /**
     * 设置房间服务器监听器
     * @param listener
     */
    fun setRoomServerListener(listener: RoomServerListener) {
        this.roomServerListener = listener
    }

    /**
     * 发送公屏聊天
     *
     * @param msg
     */
    fun sendChatMsg(uid:Long,uname:String,headImg:String,msg: String) {
        serverService!!.sendChatMsg(uid,uname,headImg,msg)
    }

    /**
     * 断开服务器连接
     */
    fun disconnect() {
        serverService?.closeRoom()
    }

    override fun onLoginSuccess(isManager: Boolean, onlineUsers: Long) {
        roomServerListener?.onServerStateInfo("聊天服务器登录成功！")
    }

    override fun onLoginRoomNoExist() {
        roomServerListener?.onRoomNoExist();
    }

    override fun onLoginFailure(errMsg: String) {
        roomServerListener?.onServerStateInfo("聊天服务器登录失败！")
    }

    override fun onServerDisconnect(msg: String) {
    }

    override fun onOnlineUsersCount(count: Long) {
    }

    override fun onChatMsg(senderId: Long, senderName: String, senderHeadImg: String, msg: String) {
        roomServerListener?.onChatMsgBRO(ChatUserInfo(senderName,senderId,0,0,senderHeadImg),msg)
    }
    override fun onSysMessage(type: Int, msgs: List<String>) {
    }
    override fun onLoginTimeout() {
    }
    companion object {
        val instance = XCManager()
    }
}
