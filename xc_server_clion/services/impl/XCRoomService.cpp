//
// Created by pixboly on 2019/1/8.
//

#include <string>
#include <vector>
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
// 根据id找房间
int XCRoomService::GetRoomById(XCRoom * room/* in */,int64_t roomId) {
    int ret = 0;
    if(NULL == room) {
        ret = -1;
        return ret;
    }
    if(roomId <= 0) {
        ret = -2;
        return ret;
    }
    // 房间不在线
    if(0 != IsRoomOnline(roomId)) {
        ret = -3;
        return ret;
    }

}
// 查找房间是否在线
int XCRoomService::IsRoomOnline(int64_t roomId) {
    int ret = 0;
    if(roomId <= 0) {
        ret = -1;
        return ret;
    }
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    redisReply * reply = (redisReply * ) redisCommand(rdConnect,RD_GET_ONLINE_ROOM,roomId);
    if(NULL == reply || reply->str == NULL) {
        ret = -3;
        return ret;
    }
    return ret;
};

//  根据房间id，取得所有房间内用户
int XCRoomService::GetRoomAllUsers(vector<XCUser*> * users,int64_t roomId) {
    int ret = 0;
    if(NULL == users || 0 >= roomId) {
        ret = -1;
        return ret;
    }
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    // 取得房间内所有用户表
    redisReply * ukReply = (redisReply * )redisCommand(rdConnect,RD_GET_ROOM_USERS_TABLE,roomId);
    vector<int64_t> userIdVector;
    if(NULL != ukReply && ukReply->type == REDIS_REPLY_ARRAY) {
        for (int i = 0; i <ukReply->elements; i++) {
            userIdVector.push_back(ukReply->element[i]->integer);
        }
        freeReplyObject(ukReply);
    }

    // 用用户取得所有用户
    for(int i = 0;i < userIdVector.size();i++) {
        XCUser * user = new XCUser;
        GetUserInfo(user,userIdVector[i]);
        if(user->uid > 0) {
            users->push_back(user);
        }
    }
}

int XCRoomService::GetUserInfo(XCUser * pUser,int64_t uid) {
    int ret;
    if(NULL == pUser || uid <= 0) {
        ret = -1;
        return ret;
    }
    // 取得昵称
    redisReply * nameReply = (redisReply*)redisCommand(rdConnect,RD_GET_ONLINE_USER_INFO_BY_KEY,uid,RD_USER_NAME_KEY);
    if(NULL != nameReply ) {
        if(NULL != nameReply->str){
            pUser->name = nameReply->str;
        }
        freeReplyObject(nameReply);
    }
    // 取得fd
    redisReply * fdReply = (redisReply*)redisCommand(rdConnect,RD_GET_ONLINE_USER_INFO_BY_KEY,uid,RD_USER_FD_KEY);
    if(NULL != fdReply) {
        if(fdReply->integer >= 0) {
            pUser->fd = atoi(fdReply->str);
        }
        freeReplyObject(fdReply);
    }

    // 取得token
    redisReply * tokenReply = (redisReply*)redisCommand(rdConnect,RD_GET_ONLINE_USER_INFO_BY_KEY,uid,RD_USER_TOKEN_KEY);
    if(NULL != tokenReply) {
        if(NULL != tokenReply->str) {
            pUser->token = tokenReply->str;
        }
        freeReplyObject(tokenReply);
    }
    // 取得roomId
    redisReply * roomIdReply = (redisReply*)redisCommand(rdConnect,RD_GET_ONLINE_USER_INFO_BY_KEY,uid,RD_USER_ROOMID_KEY);
    if(NULL != roomIdReply )  {
        if(roomIdReply->integer >= 0) {
            pUser->roomId = atol(roomIdReply->str);
        }
        freeReplyObject(roomIdReply);
    }
    pUser->uid = uid;
    return 0;
}

// 移除房间在线用户表
int XCRoomService::RemoveRoomAllUsers(int64_t roomId) {

}

// 增加在线用户
int XCRoomService::AddOnlineUser(XCUser & user){
    int ret = 0;
    if(NULL == rdConnect) {
        ret = -1;
        return ret;
    }
    string tab = RD_ONLINE_USER_MAP_NAME_HEAD;
    tab.append(TypeUtils::ltos(user.uid));
    redisReply * addReply = (redisReply *)redisCommand(rdConnect,RD_ADD_ROOM_USER,user.roomId,user.uid,tab.data());
    if(NULL == addReply) {
        if(addReply->integer <= 0) {
            ret = -2;
            return ret;
        }
        freeReplyObject(addReply);
    }
    return ret;
}

// 移除在线用户
int XCRoomService::RemoveOnlineUser(XCUser & user) {
    int ret = 0;
    if(NULL == rdConnect) {
        ret = -1;
        return ret;
    }
    redisReply * removeReply = (redisReply *)redisCommand(rdConnect,RD_REMOVE_ROOM_USER,user.roomId,user.uid);
    if(NULL != removeReply) {
        if(removeReply->integer < 0) {
            ret = -2;
            return ret;
        }
        freeReplyObject(removeReply);
    }
    return ret;
}
