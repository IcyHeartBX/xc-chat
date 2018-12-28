package com.pix.xcserverlibrary;


import com.pix.xcserverlibrary.constants.XCServerConstants;
import com.pix.xcserverlibrary.protobuf.XCAlive;
import com.pix.xcserverlibrary.protobuf.XCChatMsg;
import com.pix.xcserverlibrary.protobuf.XCLogin;
import com.pix.xcserverlibrary.protobuf.XCLogout;
import com.pix.xcserverlibrary.utils.ByteBuffer;

/**
 *
 * @author tpx
 * @version 1.0.0
 * @description
 * @modify
 */
public class XCServerWriteData implements XCServerConstants {
    public static final String TAG = XCServerWriteData.class.getSimpleName();

    // 登录包 01
    public byte[] _login(long uid, String uname, long roomId, String token, boolean isRelogin) {
        XCLogin.Builder builder = new XCLogin.Builder();
        builder.uid(uid);
        builder.roomId(roomId);
        builder.token(token);
        builder.reconnect(isRelogin);
        builder.name(uname);
        byte[] body = XCLogin.ADAPTER.encode(builder.build());
        ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.writeShortBE((short) body.length);
        byteBuffer.writeShortBE(enmu_xc_server_live_or_room);
        byteBuffer.write(body, 0, body.length);
        return (byteBuffer.toByteArray());
    }

    // 心跳包 00
    public byte[] _heart() {
        XCAlive.Builder builder = new XCAlive.Builder();
        builder.clientTime(System.currentTimeMillis());
        byte[] body = XCAlive.ADAPTER.encode(builder.build());
        ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.writeShortBE((short) body.length);
        byteBuffer.writeShortBE(enmu_xc_server_heart);
        byteBuffer.write(body, 0, body.length);
        return (byteBuffer.toByteArray());
    }

    // 登出包
    public byte[] _logout() {
        XCLogout.Builder builder = new XCLogout.Builder();
        byte[] body = XCLogout.ADAPTER.encode(builder.build());
        ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.writeShortBE((short) body.length);
        byteBuffer.writeShortBE(enmu_xc_server_logout);
        byteBuffer.write(body, 0, body.length);
        return (byteBuffer.toByteArray());
    }

    // 发送聊天 06
    public byte[] _sendChatMsg(String msg) {
        XCChatMsg.Builder builder = new XCChatMsg.Builder();
        builder.content(msg);
        byte[] body = XCChatMsg.ADAPTER.encode(builder.build());
        ByteBuffer byteBuffer = new ByteBuffer();
        byteBuffer.writeShortBE((short) body.length);
        byteBuffer.writeShortBE(enmu_xc_server_send_message);
        byteBuffer.write(body, 0, body.length);
        return (byteBuffer.toByteArray());
    }
}