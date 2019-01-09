//
// Created by pixboly on 2018/12/27.
//

#ifndef XC_SERVER_CLION_XCMANAGER_H
#define XC_SERVER_CLION_XCMANAGER_H
#include <iostream>
#include <pthread.h>
#include "../services/IOnlineUserService.h"
#include "../services/IRoomService.h"

class XCManager {
private:
    XCManager();
public:
    static XCManager * GetInstance();

public:

    void StartServer();
    void CloseServer();
    void SendAliveACK(int fd);
    void SendLoginACK(int fd,long long uid);
    void BroadcastServerClose();
    void BroadcastOnlieUserCount();
    void BroadcastSystemMessage();
    void BroadcastChatMessage(void * content);
public:
    void OnRecvServerData(int,unsigned char * ,int);

private:
    // 线程锁
    static pthread_mutex_t mutex;
    // tcp服务器句柄
    void * serverHandler;
    // 在线用户服务
    IOnlineUserService * onlineUserService;
    // 房间管理服务
    IRoomService * roomService;

};
#endif //XC_SERVER_CLION_XCMANAGER_H
