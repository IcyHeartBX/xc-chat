package com.pix.xcserverlibrary.constants;


import com.pix.xcserverlibrary.pack.PackConstants;

/**
 *
 * @author pix
 * @version 1.0.0
 * @description
 * @modify
 */
public interface XCServerConstants extends PackConstants {
    //发送包的enum
    public static final short enmu_xc_server_heart = 0x00;//心跳
    public static final short enmu_xc_server_live_or_room = 0x01;
    public static final short enmu_xc_server_logout = 0x02;
    public static final short enmu_xc_server_send_message = 0x03;

    //接受包的enum
    public static final short enum_xc_server_receive_heart_callback = 0x00;//心跳返回
    public static final short enum_xc_server_receive_logout = 0x02;//服务器连接断开
    public static final short enum_xc_server_receive_live_or_room_callback = 0x01;//登录返回
    public static final short enum_xc_server_receive_message = 0x03;//聊天信息推送
    public static final short enum_xc_server_receive_usercount = 0x04;//观众人数
    public static final short enum_xc_server_receive_system_message = 0x18;//系统消息


    //常量
    public static final short xc_server_login_success = 0;        // 登录成功
    public static final short xc_server_login_validate_error = 1; // 校验失败
    public static final short xc_server_login_room_no_exist = 2;  // 房间不存在
    public static final short xc_server_login_tichu = 3;          // 被提出
    public static final short xc_server_login_guaqi_room = 4;     // 房间挂起

}
