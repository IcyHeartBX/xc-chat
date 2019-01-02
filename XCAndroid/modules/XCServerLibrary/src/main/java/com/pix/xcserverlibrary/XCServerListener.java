package com.pix.xcserverlibrary;


import java.util.List;

/**
 * xc服务器监听器,回调所有服务器结果
 * Created by tangpengxiang on 2018/1/18.
 */

public interface XCServerListener {

    /**
     * 登录服务器成功
     * @param isManager 是否是管理员
     * @param onlineUsers 在线用户数
     */
    public void onLoginSuccess(boolean isManager, long onlineUsers);

    /**
     * 登录服务器失败
     */
    public void onLoginFailure(String errMsg);

    /**
     * 服务器主动断开连接
     * @param msg 断开消息
     */
    public void onServerDisconnect(String msg);

    /**
     * 在线用户数返回
     * @param count 用户数
     */
    public void onOnlineUsersCount(long count);

    /**
     * 聊天回调
     * @param senderId 发送者id
     * @param senderName 发送者name
     * @param senderHeadImg 发送者头像
     * @param msg 消息
     */
    public void onChatMsg(long senderId,String senderName,String senderHeadImg, String msg);

    /**
     * 系统消息
     * @param type //类型 0系统消息 1http透传消息
     * @param msgs  消息内容
     */
    public void onSysMessage(int type, List<String> msgs);

    /**
     * 连接超时
     */
    public void onLoginTimeout();
}
