package com.pix.xcserverlibrary;

/**
 * Created by tpx
 *
 * @description
 * 房间服务器服务接口，用于定义与房间服务器相关的操作
 * 用来规范标准，整理业务逻辑
 * 取消EventBus发送事件，避免逻辑混乱
 *
 */
public interface IXCServerService {
    /**
     * 设置房间服务器地址
     * @param address
     * @param port
     */
    public void setServerAddres(String address, int port) ;

    /**
     * ip
     * @return
     */
    public String getIp() ;

    /**
     * port
     * @return
     */
    public int getPort();

    /**
     * 登录服务器
     * @param uid 用户id
     * @param uname 用户昵称
     * @param roomId 房间id
     * @param token 令牌
     */
    public void loginServer(long uid, String uname, long roomId, String token) ;

    /**
     * 关闭房间,断开连接,这个方法发送了退出包,并断开tcp
     */

    public void closeRoom();

    /**
     * 设置XC服务器监听器
     * @param listener
     */
    public void setXCServerListener(XCServerListener listener);

    /**
     * 发送公屏聊天消息
     * @param msg
     */
    public void sendChatMsg(long uid,String uname,String headImg,String msg);
}
