package com.pix.xcandroid.room

import com.pix.xcandroid.bean.ChatUserInfo

/**
 * 房间View层接口
 */
interface IXCRoomView {
    /**
     * 显示聊天信息
     */
    fun showChatMsg(sender:ChatUserInfo,content:String)

    /**
     * 显示服务器状态信息
     */
    fun showServerState(state:String)
}