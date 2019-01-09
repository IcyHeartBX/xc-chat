//
// Created by pixboly on 2019/1/7.
//

#include <sstream>
#include "OnlineUserService.h"
#include "../../consts/RedisConsts.h"
#include "../../utils/TypeUtils.h"


OnlineUserService::OnlineUserService(void):isConnect(false) {
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


int OnlineUserService::AddOnlineUser(XCUser & o) {
    int ret = 0;
    if(NULL == rdConnect) {
        ret = -1;
        return ret;
    }
    // 插入在线用户
    string cmd = RD_ONLINE_USER_MAP_NAME_HEAD;
    cmd.append(TypeUtils::ltos(o.uid));
    redisReply * reply =(redisReply*) redisCommand(rdConnect,RD_PUSH_ONLINE_USER,o.uid,cmd.data());
    if(NULL == reply || 0 > reply->integer) {
        ret = -2;
        return ret;
    }
    if(NULL != reply) {
        freeReplyObject(reply);
    }

    // 插入详细信息到单个map
    redisReply * userreply =(redisReply*) redisCommand(rdConnect,RD_ADD_ONLINE_USER,o.uid,o.fd,o.uid,o.roomId,o.token.data(),o.name.data());
    if(NULL == userreply || 0 > userreply->integer) {
        ret = -2;
        return ret;
    }
    if(NULL != userreply) {
        freeReplyObject(userreply);
    }
    return ret;
}

// 移除一个用户，根据fd
int OnlineUserService::RemoveOnlineUserByFD(int fd) {
    cout<<"FUNC OnlineUserService::RemoveOnlineUserByFD(),fd"<<fd<<endl;
    // 取得所有用户
    vector<XCUser *> usersVector;
    GetAllOnlineUsers(&usersVector);
    cout<<"FUNC OnlineUserService::RemoveOnlineUserByFD(),user count:"<<usersVector.size()<<endl;
    if(usersVector.size() <= 0) { // 无在线用户
        return 0;
    }
    for(int i = 0;i < usersVector.size();i++) {
        auto user = usersVector[i];
        if(user->fd == fd) { // 找到此用户，移除
            // 从在线用户列表删除
            redisReply * delReply = (redisReply*)redisCommand(rdConnect,RD_REMOVE_ONLINE_USER,user->uid);
            if(NULL == delReply || delReply->integer < 0) {
                return -1;
            }
            freeReplyObject(delReply);
            // 移除用户map
            redisReply * delUserReply = (redisReply*)redisCommand(rdConnect,RD_REMOVE_ONLINE_USER_MAP,user->uid);
            if(NULL == delUserReply || delUserReply->integer < 0) {
                return -2;
            }
            freeReplyObject(delReply);
        }
        // 销毁User
        if(usersVector[i] != NULL) {
           delete usersVector[i];
           usersVector[i] = NULL;
        }
    }
    return 0;
}

int OnlineUserService::GetOnlineUserById(XCUser * pUser,long long uid) {
    cout<<"OnlineUserService::GetOnlineUserById(),RUN..."<<endl;
    int ret = 0;
    if(NULL == pUser) {
        ret = -1;
        return ret;
    }
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    // 此用户在线
    redisReply * reply = (redisReply *)redisCommand(rdConnect,RD_GET_ONLINE_USER,uid);
    if(NULL != reply ) {  // 有数据
        if(reply->integer < 0) {
            freeReplyObject(reply);
            return -3;
        }
        freeReplyObject(reply);
        // 取得昵称
        redisReply * nameReply = (redisReply*)redisCommand(rdConnect,RD_GET_ONLINE_USER_INFO_BY_KEY,uid,RD_USER_NAME_KEY);
        if(NULL != nameReply ) {
            if(nameReply->integer >=0) {
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
            if(tokenReply->integer >= 0) {
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
    }
    else {
        ret = -3;
    }

    return ret;
}

int OnlineUserService::GetAllOnlineUsers(vector<XCUser*> * users /* in */) {
    int ret = 0;
    if(NULL == users) {
        ret = -1;
        return ret;
    }
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    redisReply * reply =(redisReply*) redisCommand(rdConnect,RD_GET_ALL_ONLINE_USER_KEYS);
    if (reply->type == REDIS_REPLY_ARRAY) {
        for (int j = 0; j < reply->elements; j++) {
            printf("(%u) %s\n", j, reply->element[j]->str);
            // 创建User对象
            XCUser * u = new XCUser;
            GetOnlineUserById(u,atol(reply->element[j]->str));
            users->push_back(u);
        }
    }
    if(NULL != reply) {
        freeReplyObject(reply);
    }
    return ret;
}
