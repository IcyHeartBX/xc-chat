//
// Created by pixboly on 2018/12/27.
//

#ifndef XC_SERVER_CLION_PACKUTILS_H
#define XC_SERVER_CLION_PACKUTILS_H
#include <cstring>
#include "NetUtils.h"

// 数据包打包
void Pack(unsigned char * dst,int * destlen/*out*/,unsigned short datalen, unsigned short pkgcmd,unsigned char * data) {
    int count = 0;
    // 拷入包长度
    unsigned bedatalen = convert_WORD_LE_2_BE(datalen);
    memcpy(dst, &bedatalen, sizeof(unsigned short));
    count += sizeof(unsigned short);
    dst += sizeof(unsigned short);

    // 拷入包命令字
    unsigned short becmd = convert_WORD_LE_2_BE(pkgcmd);
    int cmdsize = sizeof(unsigned short);
    memcpy(dst, &becmd,cmdsize);
    count += cmdsize;
    dst += cmdsize;

    // 写入数据
    memcpy(dst,data, datalen);
    count += datalen;
    *destlen = count;
}
#endif //XC_SERVER_CLION_PACKUTILS_H
