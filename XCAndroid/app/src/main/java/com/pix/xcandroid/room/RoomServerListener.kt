package com.pix.xcandroid.room

import com.pix.xcandroid.bean.ChatUserInfo

interface RoomServerListener {
    /**
     * 房间服务器状态信息回调
     */
    fun onServerStateInfo(info:String)

    /**
     * 房间不存在返回
     */
    fun onRoomNoExist();

    /**
     * 聊天广播
     */
    fun onChatMsgBRO(sender:ChatUserInfo,msg:String)
}