//
// Created by pixboly on 2018/12/27.
//

#ifndef XC_SERVER_CLION_PACKCONSTS_H
#define XC_SERVER_CLION_PACKCONSTS_H

enum ROOM_SERVER{
    ALIVE           =   0x00,       // 心跳
    LOGIN           =   0x01,       // 登录
    LOGOUT          =   0x02,       // 登出
    CHAT_MESSAGE    =   0x03,       // 聊天消息
};

enum ROOM_SERVER_ACK {
    ALIVE_ACK       =   0x00,
    LOGIN_ACK           =   0x01,
    SERVER_CLOSE_ACK    =   0x02,
    CHAT_MESSAGE_ACK    =   0x03,
    USER_COUNT_ACK      =   0x04,
    SYSTEM_MESSAGE_ACK  =   0x18,
};
#endif //XC_SERVER_CLION_PACKCONSTS_H
