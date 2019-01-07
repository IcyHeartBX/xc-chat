//
// Created by pixboly on 2019/1/7.
//

#ifndef XC_SERVER_CLION_BEANDEFINE_H
#define XC_SERVER_CLION_BEANDEFINE_H

typedef struct _XC_User {
    int fd;
    long long uid;
    long long roomId;
    char * token;
    char * name;
} XC_User;

#endif //XC_SERVER_CLION_BEANDEFINE_H
