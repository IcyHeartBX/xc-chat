//
// Created by pixboly on 2019/1/7.
//

#ifndef XC_SERVER_CLION_BEANDEFINE_H
#define XC_SERVER_CLION_BEANDEFINE_H

#include <iostream>
using namespace std;
typedef struct _XC_User {
    int fd;
    long long uid;
    long long roomId;
    string token;
    string name;
} XCUser;

typedef struct _XC_Room {
    int roomId;
} XCRoom;
#endif //XC_SERVER_CLION_BEANDEFINE_H
