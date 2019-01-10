//
// XC房间服务实现类
// Created by pixboly on 2019/1/8.
//

#ifndef XC_SERVER_CLION_XCROOMSERVICE_H
#define XC_SERVER_CLION_XCROOMSERVICE_H

#include <hiredis.h>
#include "../IRoomService.h"

class XCRoomService: public IRoomService {
public:
    XCRoomService(void);
    ~XCRoomService() {
        if(NULL != rdConnect) {
            redisFree(rdConnect);
        }
    }
public:
    virtual int CreateRoom(int64_t roomId);
    virtual int GetRoomById(XCRoom * room/* in */,int64_t roomId);
    virtual int IsRoomOnline(int64_t roomId);
    virtual int GetRoomAllUsers(vector<XCUser*> * users,int64_t roomId);
    virtual int RemoveRoomAllUsers(int64_t roomId);
    virtual int AddOnlineUser(XCUser & user);
    virtual int RemoveOnlineUser(XCUser & user);
private:
    int GetUserInfo(XCUser * pUser,int64_t uid);
private:
    // redis连接对象
    redisContext * rdConnect;
    bool isConnect;
};
#endif //XC_SERVER_CLION_XCROOMSERVICE_H
