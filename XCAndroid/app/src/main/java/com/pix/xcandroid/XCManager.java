package com.pix.xcandroid;

import com.pix.xcserverlibrary.IXCServerService;
import com.pix.xcserverlibrary.XCServerListener;
import com.pix.xcserverlibrary.XCServerManager;

import java.util.List;

public class XCManager implements XCServerListener {
    private IXCServerService serverService;
    private long uid;
    private String uname;
    private long rid;
    private String token;
    private XCManager() {
        serverService = XCServerManager.getInstance();
        serverService.setXCServerListener(this);
    }
    private static XCManager mInstance = new XCManager();
    public static XCManager getInstance() {
        return mInstance;
    }

    /**
     * 服务器地址
     *
     * @param ip
     * @param port
     */
    public void setServerAddress(String ip, int port) {
        if (serverService != null) {
            serverService.setServerAddres(ip, port);
        }
    }

    public String getIp() {
        if(null != serverService) {
            return serverService.getIp();
        }
        return null;
    }

    public int getPort() {
        if(null != serverService) {
            return serverService.getPort();
        }
        return -1;
    }

    /**
     * 连接服务器
     *
     * @param userId 用户id
     */
    public void connect(long userId, String userName, long roomId, String token) {
        this.uid = userId;
        this.uname = userName;
        this.rid = roomId;
        this.token = token;
        if (serverService != null) {
            serverService.loginServer(userId, userName, roomId, token);
        }
    }

    /**
     * 发送公屏聊天
     *
     * @param msg
     */
    public void sendChatMsg(String msg) {
        serverService.sendChatMsg(msg);
    }

    /**
     * 断开服务器连接
     */
    public void disconnect() {
        if (null != serverService) {
            serverService.closeRoom();
        }
    }

    @Override
    public void onLoginSuccess(boolean isManager, long onlineUsers) {

    }

    @Override
    public void onLoginFailure(String errMsg) {

    }

    @Override
    public void onServerDisconnect(String msg) {

    }

    @Override
    public void onOnlineUsersCount(long count) {

    }

    @Override
    public void onChatMsg(String sender, String msg) {

    }

    @Override
    public void onSysMessage(int type, List<String> msgs) {

    }

    @Override
    public void onLoginTimeout() {

    }
}
