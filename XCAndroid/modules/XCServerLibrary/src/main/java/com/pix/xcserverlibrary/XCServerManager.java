package com.pix.xcserverlibrary;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import com.pix.xcserverlibrary.constants.XCServerConstants;
import com.pix.xcserverlibrary.protobuf.*;
import com.pix.xcserverlibrary.tcp.ITcpSocketClient;
import com.pix.xcserverlibrary.tcp.TcpNIOSocketClient;
import com.pix.xcserverlibrary.tcp.TcpSocketListener;
import com.pix.xcserverlibrary.utils.LogUtils;
import com.pix.xcserverlibrary.utils.NetUtils;

/**
 * Created by tpx
 *
 * @description XC服务器管理类, 处理服务器连接, 收发包操作
 */
public class XCServerManager implements TcpSocketListener, XCServerConstants, XCServerReadData.OnReceiveMessage, IXCServerService , Handler.Callback{
    public static final String TAG = XCServerManager.class.getSimpleName();
    public static final String XC_VERSION = "1.0.0";
    private final static int HEART_INTERVAL = 3000;
    private final static int HEART_DEAD_TIME = HEART_INTERVAL * 6;
    private final static int ABANDON_CONNECT_TIME = HEART_INTERVAL * 20;
    private final static int SOCKET_TIMEOUT_TIME = 5000;
    private final static int RECONNECT_TIME_INTERVAL = HEART_INTERVAL;
    private final static int HANDLER_HEART = 0;
    private final static int HANDLER_HEART_DEAD = 1;
    private final static int HANDLER_ABADON_CONNECT = 2;
    private final static int HANDLER_LOGIN = 3;
    private final static int HANDLER_RELOGIN = 4;
    private static final int SOCKET_TIME_OUT = 5;
    // 是否存活,根据心跳判断
    private boolean isAlive = false;
    // 是否重连
    private boolean enableConnect = true;
    private static XCServerManager roomServerManager = new XCServerManager();

    private String mAddress = "";
    private int mPort = -1;

    private long userId;
    private String username;
    private long roomId;
    private String token;

    //roomServer新服务器业务所需要的变量
    private long serverTime;//心跳
    private Handler mServerHandler;//
    private Handler mMainHandler;
    private boolean isReadyToLogin = false;//服务器地址是否已经获得
    private XCServerReadData mRoomServerReadData;
    private XCServerWriteData mRoomServerWriteData;
    private RoomServerHandlerThread mRoomServerHandlerThread;
    private ITcpSocketClient mTcpNIOSocketClient;
    private XCServerListener xcServerListener;

    public XCServerManager() {
        mRoomServerHandlerThread = new RoomServerHandlerThread("roomServerManager");
        mRoomServerHandlerThread.start();
        mServerHandler = new Handler(mRoomServerHandlerThread.getLooper(), mRoomServerHandlerThread);
        mMainHandler = new Handler(this);
        mRoomServerReadData = new XCServerReadData();
        mRoomServerWriteData = new XCServerWriteData();
        mRoomServerReadData.setOnReceiveMessage(this);
    }

    /**
     * 连接服务器必须要调用的函数
     */
    @Override
    public void loginServer(long userId, String uname, long roomId, String token) {
        LogUtils.d(TAG, "_login()," + "#1#连接房间服务器(" + userId + ")");
        this.userId = userId;
        this.username = uname;
        this.roomId = roomId;
        this.token = token;
        enableConnect = true;
        //**设置属性参数,发送登录msg
        if (isReadyToLogin) {
            mServerHandler.removeMessages(HANDLER_HEART);
            mServerHandler.removeMessages(HANDLER_LOGIN);
            mServerHandler.removeMessages(HANDLER_HEART_DEAD);
            mServerHandler.sendEmptyMessage(HANDLER_LOGIN);
            mRoomServerReadData.clearCache();
        }
        else {
            if(null != xcServerListener) {
                xcServerListener.onLoginFailure("loginServer(),ip 或 端口错误:ip:" + getIp() + ",port:" + getPort());
            }
            return ;
        }
        mServerHandler.sendEmptyMessageDelayed(HANDLER_ABADON_CONNECT, HEART_INTERVAL * 6);
    }

    @Override
    public void closeRoom() {
        LogUtils.d(TAG, "closeRoom()");
        long start = SystemClock.currentThreadTimeMillis();
        isReadyToLogin = false;
        sendServerData(mRoomServerWriteData._logout());
        mServerHandler.removeCallbacksAndMessages(null);
        mMainHandler.removeCallbacksAndMessages(null);
        if (mTcpNIOSocketClient != null) {
            mTcpNIOSocketClient.setTcpNioListener(null);
        }
        if (mTcpNIOSocketClient != null) {
            mTcpNIOSocketClient.close(true);
            mTcpNIOSocketClient.destroy();
            mTcpNIOSocketClient = null;
        }
        mRoomServerReadData.clearCache();
        long end = SystemClock.currentThreadTimeMillis() - start;
    }

    @Override
    public void setXCServerListener(XCServerListener listener) {
        xcServerListener = listener;
    }

    @Override
    public void sendChatMsg(long uid,String uname,String headImg,String msg) {
        sendServerData(mRoomServerWriteData._sendChatMsg(uid,uname,headImg,msg));
    }

    @Override
    public void onConnect(ITcpSocketClient tcpNIOSocketClient) {
        LogUtils.d(TAG, "onConnect(),#2#onSocketConnectSuccess,Send Login packet ");
        if(!enableConnect) {
            if(null != mTcpNIOSocketClient) {
                mTcpNIOSocketClient.close();
            }
            return ;
        }
        sendServerData(mRoomServerWriteData._login(userId, username, roomId, token, true));
        isAlive = true;
        mServerHandler.removeCallbacksAndMessages(null);
        mServerHandler.sendEmptyMessage(HANDLER_HEART);
    }

    @Override
    public void onReceiveData(ITcpSocketClient tcpNIOSocketClient, final byte[] data) {
        if(null == mMainHandler) {
            return ;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                mRoomServerReadData.unpack(data);
                } catch (Exception e) {
                    LogUtils.w(TAG, "onSocketData(),error...");
                    mRoomServerReadData.clearCache();
                }
            }
        });
    }

    @Override
    public void onConnectError(ITcpSocketClient tcpNIOSocketClient, String message, Throwable throwable) {
        LogUtils.e(TAG, "onConnectError(),message:" + message, throwable);
        if(null != mRoomServerReadData) {
            mRoomServerReadData.clearCache();
        }
        mServerHandler.removeMessages(SOCKET_TIME_OUT);
        mServerHandler.sendEmptyMessage(HANDLER_HEART_DEAD);
    }

    @Override
    public void setServerAddres(String address, int port) {
        this.mAddress = address;
        this.mPort = port;
        if (null != mTcpNIOSocketClient) {
            mTcpNIOSocketClient = null;
        }
        mTcpNIOSocketClient = TcpNIOSocketClient.getSingleInstance();
        mTcpNIOSocketClient.setTcpNioListener(this);
        mTcpNIOSocketClient.setHostAndPort(this.mAddress, this.mPort);

        LogUtils.d(TAG, "onEventRoomServerConfig(),获取服务器地址" + mAddress + ":" + mPort);
        if(!NetUtils.isIpMatch(address)) {
            isReadyToLogin = false;
            if(null != xcServerListener) {
                xcServerListener.onLoginFailure("setServerAddres(),ip错误,ip:" + address);
            }
            return ;
        }
        isReadyToLogin = true;
    }

    @Override
    public String getIp() {
        return mAddress;
    }

    @Override
    public int getPort() {
        return mPort;
    }

    // 主线程的Handler
    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    private class RoomServerHandlerThread extends HandlerThread implements Handler.Callback {
        public RoomServerHandlerThread(String name) {
            super(name);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_HEART:     // 心跳
                    LogUtils.d(TAG, "handleMessage(),HANDLER_HEART,XC_VERSION:" + XC_VERSION);
                    sendServerData(mRoomServerWriteData._heart());
                    mServerHandler.removeMessages(HANDLER_HEART);
                    mServerHandler.removeMessages(HANDLER_LOGIN);
                    mServerHandler.sendEmptyMessageDelayed(HANDLER_HEART, HEART_INTERVAL);
                    mServerHandler.sendEmptyMessageDelayed(HANDLER_HEART_DEAD, HEART_DEAD_TIME);
                    break;

                case HANDLER_HEART_DEAD:              // 无心跳
                    LogUtils.d(TAG, "handleMessage(),HANDLER_HEART_DEAD,重连");
                    if(isAlive && enableConnect) {
                        mServerHandler.removeMessages(HANDLER_ABADON_CONNECT);
                        mServerHandler.sendEmptyMessageDelayed(HANDLER_ABADON_CONNECT, ABANDON_CONNECT_TIME);
                    }
                    isAlive = false;
                    mServerHandler.removeMessages(HANDLER_HEART);
                    mServerHandler.removeMessages(HANDLER_LOGIN);
                    mServerHandler.removeMessages(HANDLER_RELOGIN);
                    mServerHandler.removeMessages(HANDLER_HEART_DEAD);
                    if(enableConnect) {
                        // 立即重连
                        mServerHandler.sendEmptyMessage(HANDLER_RELOGIN);
//                    mServerHandler.sendEmptyMessageDelayed(HANDLER_RELOGIN, RECONNECT_TIME_INTERVAL);
                        mServerHandler.sendEmptyMessageDelayed(HANDLER_HEART_DEAD, RECONNECT_TIME_INTERVAL);
                    }
                    break;
                case HANDLER_ABADON_CONNECT:        // 服务器断开
                    isAlive = false;
                    enableConnect = false;
                    LogUtils.d(TAG, "handleMessage(),HANDLER_ABADON_CONNECT 服务器连接失败，关闭连接，回调服务器连接失败~");
                    mServerHandler.removeCallbacksAndMessages(null);
                    if(null != mTcpNIOSocketClient) {
                        mTcpNIOSocketClient.close();
                    }
                    // 发到主线程
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            xcServerListener.onLoginFailure("连接超时");
                            xcServerListener.onLoginTimeout();
                        }
                    });

                    //退出房间
                    break;
                case HANDLER_LOGIN:         // 登录
                    LogUtils.d(TAG, "handleMessage(),HANDLER_LOGIN,登录服务器!");
                    mServerHandler.removeMessages(HANDLER_HEART);
                    mServerHandler.removeMessages(HANDLER_LOGIN);
                    mServerHandler.removeMessages(SOCKET_TIME_OUT);
                    if(!enableConnect) {
                        break;
                    }
                    if (null != mTcpNIOSocketClient) {
                        if (!mServerHandler.hasMessages(SOCKET_TIME_OUT)) {
                            mServerHandler.sendEmptyMessageDelayed(SOCKET_TIME_OUT,SOCKET_TIMEOUT_TIME);//5秒房间连接超时
                        }
                        mTcpNIOSocketClient.connect();
                    }
                    mRoomServerReadData.clearCache();
                    break;
                case HANDLER_RELOGIN:   // 重新登录
                    isAlive = false;
                    LogUtils.d(TAG, "handleMessage(),HANDLER_RELOGIN,reconnect server,3s后重连");
                    mServerHandler.removeMessages(HANDLER_HEART);
                    mServerHandler.removeMessages(HANDLER_RELOGIN);
                    mServerHandler.removeMessages(HANDLER_LOGIN);
                    mServerHandler.removeMessages(HANDLER_HEART_DEAD);
                    if(!enableConnect) {
                        break;
                    }
                    if(null != mTcpNIOSocketClient) {
                        mTcpNIOSocketClient.close();
                    }
                    mServerHandler.sendEmptyMessageDelayed(HANDLER_LOGIN,RECONNECT_TIME_INTERVAL);
                    break;
                case SOCKET_TIME_OUT:  // 5s socket连接超时
                    LogUtils.d(TAG, "handleMessage(),SOCKET_TIME_OUT,socket连接超时");
                    isAlive = false;
                    mServerHandler.removeMessages(SOCKET_TIME_OUT);
                    // 重连服务器
                    if(enableConnect) {
                        mServerHandler.sendEmptyMessage(HANDLER_RELOGIN);
                    }
                    break;
            }
            return false;
        }
    }

    // 服务器包返回,解包结果
    @Override
    public void onReceive(com.squareup.wire.Message message) {
        LogUtils.d(TAG, "onReceive(),处理数据： " + message);
        mServerHandler.removeMessages(HANDLER_RELOGIN);
        mServerHandler.removeMessages(HANDLER_LOGIN);             // 移除登录
        mServerHandler.removeMessages(HANDLER_ABADON_CONNECT);    // 移除连接
        mServerHandler.removeMessages(HANDLER_HEART_DEAD);        // 移除心跳死亡
        mServerHandler.removeMessages(SOCKET_TIME_OUT);
        if (null == message) {
            return;
        }
        if (message instanceof XCAliveACK) {  // 心跳
            XCAliveACK aliveACK = (XCAliveACK) message;
            if (aliveACK.serverTime == null) return;
            serverTime = aliveACK.serverTime;
            LogUtils.d(TAG, "onEventHeart(),heart beat server time " + serverTime);
        } else if (message instanceof XCLoginACK) { //登录回包
            onLoginRcv((XCLoginACK) message);
        } else if (message instanceof XCServerCloseBRO) { // 房间关闭
            XCServerCloseBRO serverCloseBRO = (XCServerCloseBRO) message;
            if (null != xcServerListener) {
                xcServerListener.onServerDisconnect(serverCloseBRO.msg);
            }
        } else if (message instanceof XCOnlineCountBRO) { // 用户数
            XCOnlineCountBRO onlineCountBRO = (XCOnlineCountBRO) message;
            if (null != xcServerListener && null != onlineCountBRO.count) {
                xcServerListener.onOnlineUsersCount(onlineCountBRO.count);
            }
        } else if (message instanceof XCChatMsgBRO) { // 公屏聊天消息
            XCChatMsgBRO chatMsgBRO = (XCChatMsgBRO) message;
            if (null != xcServerListener && null != chatMsgBRO.player && null != chatMsgBRO.chat) {
                ChatPlayer player = chatMsgBRO.player;
                XCChatMsg ssc6 = chatMsgBRO.chat;
                xcServerListener.onChatMsg(player.id,player.name,player.headImg ,ssc6.content);
            }
        }
        else if (message instanceof XCSystemMsgBRO) { // 系统消息
            XCSystemMsgBRO sysMsgBRO = (XCSystemMsgBRO) message;
            if (xcServerListener != null) {
                int type = 0;
                if (null != sysMsgBRO.type) {
                    type = sysMsgBRO.type;
                }
                xcServerListener.onSysMessage(type, sysMsgBRO.content);
            }
        }
    }

    public synchronized void sendServerData(byte[] bytes) {
        if (mTcpNIOSocketClient != null) {
            mTcpNIOSocketClient.sendData(bytes);
        }
    }

    /**
     * 登录服务器返回包
     *
     * @param loginACK
     */
    public void onLoginRcv(XCLoginACK loginACK) {

        if (loginACK.result == xc_server_login_success || loginACK.result == xc_server_login_guaqi_room) {
            LogUtils.d(TAG, "onLoginRcv(),#3# login back，login success！");
            if (null != xcServerListener) {
                long users = 0;
                boolean isManager = false;
                if (null != loginACK.onlineUsers) {
                    users = loginACK.onlineUsers;
                }
                if (null != loginACK.isManager) {
                    isManager = loginACK.isManager;
                }
                xcServerListener.onLoginSuccess(isManager, users);
            }
        } else {
            if (null != xcServerListener) {
                xcServerListener.onLoginFailure("登录错误,state:" + loginACK.result);
            }
            if(loginACK.result == xc_server_login_room_no_exist) {
                xcServerListener.onLoginRoomNoExist();
            }
        }
    }

    //*************end**************//
    public static XCServerManager getInstance() {
        return roomServerManager;
    }
}
