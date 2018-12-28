//
// Created by pixboly on 2018/12/27.
//

#ifndef XC_SERVER_CLION_NETUTILS_H
#define XC_SERVER_CLION_NETUTILS_H
/*
    WORD 类型大端转小端
 */
unsigned short convert_WORD_BE_2_LE(unsigned short value) {
    return (value & 0x00FFU)<< 8 |
           (value & 0xFF00U) >> 8;
}

/**
  WORD类型小端转大端

 @param value 小端WORD
 @return 大端WORD
 */
unsigned short convert_WORD_LE_2_BE(unsigned short value) {
    return (value & 0x00FFU)<< 8 |
           (value & 0xFF00U) >> 8;
}

#endif //XC_SERVER_CLION_NETUTILS_H
