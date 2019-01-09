//
// Created by pixboly on 2019/1/8.
//

#include "XCRoomService.h"
#include "../../consts/RedisConsts.h"

XCRoomService::XCRoomService() {
    // 连接redis
    // 超时时间
    struct timeval timeout = { 1, 500000 }; // 1.5 seconds
    // 连接redis服务器
    rdConnect = redisConnectWithTimeout(RD_HOST,RD_PORT, timeout);
    if (rdConnect == NULL || rdConnect->err) {
        isConnect = false;
        if (rdConnect) {
            printf("Connection error: %s,dont's connect Redis Server!\n", rdConnect->errstr);
            redisFree(rdConnect);
        } else {
            printf("Connection error: can't allocate redis context\n");
        }
    }
    else {
        isConnect = true;
    }
}

// 创建一个房间
int XCRoomService::CreateRoom(int  roomId) {
    // 在redis增加在线房间
    
}

