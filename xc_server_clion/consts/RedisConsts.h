//
//  RedisConsts.h
//  AQPollServer
//  Redis服务器常亮定义
//  Created by PixBoly on 2018/1/30.
//  Copyright © 2018年 pix. All rights reserved.
//

#ifndef RedisConsts_h
#define RedisConsts_h

#define RD_HOST     "127.0.0.1"         // redis 服务ip
#define RD_PORT     6379                // redis 端口




// 增加在线用户,存储用户的名字map
#define RD_PUSH_ONLINE_USER     "HSET xc_online_users_hash %lld %s"

#define RD_ONLINE_USER_MAP_NAME_HEAD "xc_online_users_hash:"



// 取得所有在线用户
#define RD_GET_ALL_ONLINE_USER  "HVALS aq_online_users_hash"

// 存储用户的map信息
#define RD_ADD_ONLINE_USER      "HSET xc_online_users_hash:%lld fd %d uid %lld roooId %lld token %s name %s"

// 根据用户id取得用户
#define RD_GET_ONLINE_USER      "HGET aq_online_users_hash %lld"

#endif /* RedisConsts_h */