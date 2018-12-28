package com.pix.xcserverlibrary;

import com.pix.xcserverlibrary.constants.XCServerConstants;
import com.pix.xcserverlibrary.protobuf.*;
import com.pix.xcserverlibrary.utils.ByteBuffer;
import com.pix.xcserverlibrary.utils.LogUtils;
import com.squareup.wire.Message;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class XCServerReadData implements XCServerConstants {
    private static final String TAG = "XCServerReadData";
    private byte[] cache = new byte[1024 * 100];
    private int cachePosition = 0;


    private OnReceiveMessage onReceiveMessage;

    public XCServerReadData() {
    }

    public void clearCache() {
        cachePosition = 0;
        Arrays.fill(cache, (byte) 0);
    }

    public void unpack(byte[] datas) throws Exception {


        System.arraycopy(datas, 0, cache, cachePosition, datas.length);
        cachePosition = cachePosition + datas.length;
        LogUtils.d(TAG, "unpack(),读取数据：当前cachePosition " + cachePosition + ",datas length " + datas.length);
        boolean recycle = true;
        while (recycle) {
            ByteBuffer byteBuffer = new ByteBuffer(cache);
            int bodyLength = byteBuffer.readShortBE();
            int head = byteBuffer.readShortBE();
            final int dataLength = 2 + 2 + bodyLength;
            LogUtils.d(TAG, "unpack(),解析数据:当前cachePosition：" + cachePosition + " head " + head + " body length " + bodyLength + "，该包占用长度：" + dataLength);
            if (dataLength > cachePosition) {
                return;
            }
            Message message = null;
            try {
                byte[] body = new byte[bodyLength];
                byteBuffer.read(body, 0, bodyLength);
                switch (head) {
                    case enum_xc_server_receive_heart_callback:
                        LogUtils.d(TAG, "unpack(),enum_xc_server_receive_heart_callback,body length " + body.length);
                        message = receive_heart_callback(body);
                        break;
                    case enum_xc_server_receive_live_or_room_callback:
                        message = receive_login_callback(body);
                        break;
                    case enum_xc_server_receive_logout:
                        message = receive_disconnect_callback(body);
                        break;
                    case enum_xc_server_receive_message:
                        message = receive_chatmsg_callback(body);
                        break;
                    case enum_xc_server_receive_usercount:
                        message = receive_usercount_callback(body);
                        break;
                    case enum_xc_server_receive_system_message:
                        LogUtils.d(TAG, "unpack(),enum_xc_server_receive_system_message,body length " + body.length);
                        message = receive_sysmsg_callback(body);
                        break;
                    default:
                        //出现一个错误
                        LogUtils.d(TAG, "unpack(),enum_room_server_undefined,body length " + body.length);
                        break;
                }
                cachePosition -= dataLength;
                System.arraycopy(cache, dataLength, cache, 0, cachePosition);
                if (onReceiveMessage != null && message != null) {
                    onReceiveMessage.onReceive(message);
                }
                if (cachePosition < 4) {
                    recycle = false;
                }
            } catch (Exception e) {
                clearCache();
                throw e;
            }
        }
    }

    // 心跳回包
    public XCAliveACK receive_heart_callback(byte[] body) throws IOException {
        return XCAliveACK.ADAPTER.decode(body);
    }

    // 登录回包
    public XCLoginACK receive_login_callback(byte[] body) throws IOException {
        return XCLoginACK.ADAPTER.decode(body);
    }

    // 服务器连接关闭
    public XCServerCloseBRO receive_disconnect_callback(byte[] body) throws IOException {
        return XCServerCloseBRO.ADAPTER.decode(body);
    }

    // 用户数广播
    public XCOnlineCountBRO receive_usercount_callback(byte[] body) throws IOException {
        return XCOnlineCountBRO.ADAPTER.decode(body);
    }

    public XCChatMsgBRO receive_chatmsg_callback(byte [] body) throws IOException {
        return XCChatMsgBRO.ADAPTER.decode(body);
    }

    // 系统消息
    public XCSystemMsgBRO receive_sysmsg_callback(byte [] body) throws IOException {
        return XCSystemMsgBRO.ADAPTER.decode(body);
    }

    public OnReceiveMessage getOnReceiveMessage() {
        return onReceiveMessage;
    }

    public void setOnReceiveMessage(OnReceiveMessage onReceiveMessage) {
        this.onReceiveMessage = onReceiveMessage;
    }

    public interface OnReceiveMessage {
        public void onReceive(Message message);
    }
}



