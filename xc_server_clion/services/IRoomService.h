//
// 房间服务接口，用来指定房间操作标准
// Created by pixboly on 2019/1/8.
//

#ifndef XC_SERVER_CLION_IROOMSERVICE_H
#define XC_SERVER_CLION_IROOMSERVICE_H
class IRoomService {
public:
     /**
      * 创建房间
      * @param roomId 返回房间id
      * @return 0 创建成功
      *         <0 创建失败
      */
     virtual int CreateRoom(int * roomId /* out */)=0;
};
#endif //XC_SERVER_CLION_IROOMSERVICE_H
