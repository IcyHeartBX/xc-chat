//
// Created by pixboly on 2019/1/8.
//

#include <string>
#include "XCRoomService.h"
#include "../../consts/RedisConsts.h"
#include "../../utils/TypeUtils.h"

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
int XCRoomService::CreateRoom(int64_t  roomId) {
    // 在redis增加在线房间
    int ret = 0;
    if(roomId <= 0) {
        ret = -1;
        return ret;
    }
    // 检查redis连接
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    // 插入房间到房间列表
    string cmd = RD_ONLINE_ROOM_MAP_HEAD;
    cmd.append(TypeUtils::ltos(roomId));
    redisReply * reply = (redisReply*)redisCommand(rdConnect,RD_PUSH_ONLINE_ROOM,roomId,cmd.data());
    if(NULL == reply||reply->integer < 0) {
        ret = -3;
        return ret;
    }
    // 增加本房间相关配置信息map
    redisReply * roomReply = (redisReply*)redisCommand(rdConnect,RD_ADD_ONLINE_ROOM_MAP,roomId,roomId);
    if(NULL != roomReply || roomReply->integer < 0) {
        ret = -4;
        return ret;
    }
    return 0;
}

