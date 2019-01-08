//
// Created by pixboly on 2019/1/7.
//

#include <sstream>
#include "OnlineUserService.h"
#include "../../consts/RedisConsts.h"


string ltos(long l)
{
    ostringstream os;
    os<<l;
    string result;
    istringstream is(os.str());
    is>>result;
    return result;

}

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
    cmd.append(ltos(o.uid));
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

int OnlineUserService::GetOnlineUserById(XCUser * pUser,long long uid) {
    int ret = 0;
    if(NULL == pUser) {
        ret = -1;
        return ret;
    }
    if(NULL == rdConnect) {
        ret = -2;
        return ret;
    }
    redisReply * reply = (redisReply *)redisCommand(rdConnect,RD_GET_ONLINE_USER,uid);
//    if(NULL != reply && reply->integer >= 0) {
//        XCUser user;
//        user.ParseFromArray(reply->str, reply->len);
//        pUser->CopyFrom(user);
//    }
//    else {
//        ret = -3;
//        return ret;
//    }
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
    redisReply * reply =(redisReply*) redisCommand(rdConnect,RD_GET_ALL_ONLINE_USER);
    if (reply->type == REDIS_REPLY_ARRAY) {
        for (int j = 0; j < reply->elements; j++) {
            printf("(%u) %s\n", j, reply->element[j]->str);

        }
    }
    if(NULL != reply) {
        freeReplyObject(reply);
    }
    return ret;
}
