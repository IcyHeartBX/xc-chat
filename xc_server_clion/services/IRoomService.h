//
// 房间服务接口，用来指定房间操作标准
// Created by pixboly on 2019/1/8.
//

#ifndef XC_SERVER_CLION_IROOMSERVICE_H
#define XC_SERVER_CLION_IROOMSERVICE_H
using namespace std;
#include <iostream>
#include "../define/BeanDefine.h"
class IRoomService {
public:
     /**
      * 创建房间
      * @param roomId 房间id，可以使用用户id
      * @return 0 创建成功
      *         <0 创建失败
      */
     virtual int CreateRoom(int64_t roomId )=0;

     /**
      * 根据房间id取得房间对象
      */
     virtual int GetRoomById(XCRoom * room/* in */,int64_t roomId) = 0;
     /**
      * 根据id查找房间是否在线
      */
     virtual int IsRoomOnline(int64_t roomId) = 0;
};
#endif //XC_SERVER_CLION_IROOMSERVICE_H
