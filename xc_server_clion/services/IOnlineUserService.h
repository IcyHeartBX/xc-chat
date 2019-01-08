//
// 在现用户服务接口，提供在线用户操作
// Created by pixboly on 2019/1/7.
//

#ifndef XC_SERVER_CLION_IONLINEUSERSERVICE_H
#define XC_SERVER_CLION_IONLINEUSERSERVICE_H

#include <vector>
#include "../define/BeanDefine.h"

using namespace std;

class IOnlineUserService {
public:
    /**
     增加一个在线用户

     @param o 要增加的用户的对象引用
     @return 正确返回0
     */
    virtual int AddOnlineUser(XCUser & o) = 0;


    /**
     根据uid取得用户对象

     @param pUser 用户对象指针
     @param uid 用户id
     @return 正确返回0
     */
    virtual int GetOnlineUserById(XCUser * pUser,long long uid) = 0;

    /**
     取得所有在线用户

     @param users 在线用户集合
     @return 正确返回0
     */
    virtual int GetAllOnlineUsers(vector<XCUser*> * users /* in */) = 0;
};

#endif //XC_SERVER_CLION_IONLINEUSERSERVICE_H
