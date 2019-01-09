//
// Created by pixboly on 2018/12/27.
//

#ifndef XC_SERVER_CLION_PACKCONSTS_H
#define XC_SERVER_CLION_PACKCONSTS_H

// 客户端包定义
enum ROOM_SERVER{
    ALIVE           =   0x00,       // 心跳
    LOGIN           =   0x01,       // 登录
    LOGOUT          =   0x02,       // 登出
    CHAT_MESSAGE    =   0x03,       // 聊天消息
};
// 服务端回包定义
enum ROOM_SERVER_ACK {
    ALIVE_ACK           =   0x00,   //心跳返回
    LOGIN_ACK           =   0x01,   //登录返回
    SERVER_CLOSE_ACK    =   0x02,
    CHAT_MESSAGE_ACK    =   0x03,
    USER_COUNT_ACK      =   0x04,
    SYSTEM_MESSAGE_ACK  =   0x18,
};
// 登录房间结果
enum LOGIN_ROOM_RESULT {
    LoginSuccess        =   0x00,     // 登录成功
    CheckFailure        =   0x01,     // 校验失败
    RoomNoExist         =   0x02,     // 房间不存在
};
#endif //XC_SERVER_CLION_PACKCONSTS_H
