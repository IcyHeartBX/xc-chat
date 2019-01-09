//
//  RedisConsts.h
//  AQPollServer
//  Redis服务器常亮定义
//  Created by PixBoly on 2018/1/30.
//  Copyright © 2018年 pix. All rights reserved.
//

#ifndef RedisConsts_h
#define RedisConsts_h

// redis相关
#define RD_HOST     "127.0.0.1"         // redis 服务ip
#define RD_PORT     6379                // redis 端口


// 用户管理

// 用户信息name key
#define RD_USER_NAME_KEY        "name"
// 用户信息fd key
#define RD_USER_FD_KEY          "fd"
// 用户信息 roomId KEY
#define RD_USER_ROOMID_KEY      "roomId"
// 用户信息 token KEY
#define RD_USER_TOKEN_KEY       "token"

// 增加在线用户,存储用户的名字map
#define RD_PUSH_ONLINE_USER     "HSET xc_online_users_hash %lld %s"
// 移除在线用户
#define RD_REMOVE_ONLINE_USER   "HDEL xc_online_users_hash %lld"
// 用户map，keyt头
#define RD_ONLINE_USER_MAP_NAME_HEAD "xc_online_users_hash:"
// 取得所有在线用户
#define RD_GET_ALL_ONLINE_USER_KEYS  "HKEYS xc_online_users_hash"
// 存储用户的map信息
#define RD_ADD_ONLINE_USER      "HSET xc_online_users_hash:%lld fd %d uid %lld roomId %lld token %s name %s"
// 移除用户的map信息
#define RD_REMOVE_ONLINE_USER_MAP   "DEL xc_online_users_hash:%lld"
// 取出存储的用户的map信息,根据key
#define RD_GET_ONLINE_USER_INFO_BY_KEY " HGET xc_online_users_hash:%lld %s"
// 根据用户id取得用户
#define RD_GET_ONLINE_USER      "HGET xc_online_users_hash %lld"


// 房间管理

// 增加在线房间
#define RD_PUSH_ONLINE_ROOM    "HSET xc_online_room_hash %lld %s"




#endif /* RedisConsts_h */