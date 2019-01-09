//
// Created by pixboly on 2018/12/27.
//

#include "XCManager.h"
#include "../protoc/xc_protoc.pb.h"
#include "../utils/NetUtils.h"
#include "../utils/PackConsts.h"
#include "../utils/PackUtils.h"
#include "../tcp/tcp_poll_server.h"
#include "../services/impl/OnlineUserService.h"
#include "../services/impl/XCRoomService.h"

using namespace std;
using namespace xc::protoc;

#define PORT        7777

pthread_mutex_t XCManager::mutex;

XCManager::XCManager() {
    pthread_mutex_init(&mutex,NULL);
}

XCManager * XCManager::GetInstance(){
    pthread_mutex_lock(&mutex);
    static XCManager instance;
    pthread_mutex_unlock(&mutex);
    return &instance;
}

void XCManager::OnRecvServerData(int fd,unsigned char * data ,int len) {
    try {
        //cout<<"FUNC XCManager::OnRecvServerData(),RUN..."<<endl;
        // 包长度
        unsigned short dlen = convert_WORD_BE_2_LE(*((unsigned short *)data));
        // 包类型
        unsigned short cmd =convert_WORD_BE_2_LE(*((unsigned short*) (data + 2)));
        //printf("FUNC ServerRecvCallback(),buf[20]:%c,len:%d,datalength:%d,cmd:%d\n",data[20],len,dlen,cmd);

        switch (cmd) {
            case ROOM_SERVER::ALIVE: // 心跳 0x00
            {
                //  cout<<"FUNC ServerRecvCallback(),heartbeat:"<<endl;
                XCAlive heart;
                bool flag= heart.ParseFromArray(data + 4,dlen);
                if(flag) {
                    SendAliveACK(fd);
                }
            }
                break;
            case ROOM_SERVER::LOGIN:      // 登录
            {
                XCLogin login;
                bool flag= login.ParseFromArray(data + 4,dlen);
                if(flag) {
                    //cout<<"FUNC ServerRecvCallback(),uid:"<<login.uid()<<",roomid:"<<login.roomid()<<",token:"<<login.token()<<endl;
                    printf("FUNC ServerRecvCallback(),isConnect:%d\n",login.reconnect());
                    // 检查房间是否存在
                    if(NULL == roomService || 0 != roomService->IsRoomOnline(login.roomid())) {
                        SendLoginACK(fd,login.uid(),LOGIN_ROOM_RESULT::RoomNoExist);
                        return ;
                    }
                    if(NULL != onlineUserService) {     // 加到Redis中
                        XCUser user ;
                        user.fd = fd;
                        user.uid = login.uid();
                        user.roomId = login.roomid();
                        user.name = login.name();
                        user.token = login.token();
                        int ret = onlineUserService->AddOnlineUser(user);
                        if(ret == 0) {
                            cout<<"增加新用户成功!"<<endl;
                        }
                    }
                    SendLoginACK(fd,login.uid(),LOGIN_ROOM_RESULT::LoginSuccess);
                }
            }
                break;
            case ROOM_SERVER::LOGOUT:           // 登出
                // 用户登出，从在线表移除
            {
                XCLogout logout;
                bool flag = logout.ParseFromArray(data + 4,dlen);
                if(flag) {
                    cout<<"FUNC ServerRecvCallback(),ROOM_SERVER::LOGOUT,fd:"<<fd<<endl;
                    if(NULL != onlineUserService) {
                        onlineUserService->RemoveOnlineUserByFD(fd);
                    }
                }
            }
                break;
            case ROOM_SERVER::CHAT_MESSAGE:     // 公屏聊天
                XCChatMsg chatMsg;
                bool flag = chatMsg.ParseFromArray(data + 4,dlen);
                if(flag) {
                    BroadcastChatMessage(&chatMsg);
                }
                break;

        }
    } catch (...) {
        cout<<"XCManager::OnRecvServerData(),error"<<endl;
    }

}

void recvServerDataCallback(int fd,unsigned char * buf,int buflen) {
    XCManager::GetInstance()->OnRecvServerData(fd, buf, buflen);
}

void XCManager::StartServer() {
    onlineUserService = new OnlineUserService;
    roomService = new XCRoomService;
    // 临时创建一个房间
    roomService->CreateRoom(10000);
    tcp_poll_server_init(&serverHandler, PORT);
    tcp_poll_server_set_recv_callback(serverHandler, recvServerDataCallback);
    tcp_poll_server_start(serverHandler);
}

void XCManager::CloseServer() {
    tcp_poll_server_close(serverHandler);
    tcp_poll_server_destroy(&serverHandler);
    if(NULL != onlineUserService) {
        delete onlineUserService;
        onlineUserService = NULL;
    }
    if(NULL != roomService) {
        delete roomService;
        roomService = NULL;
    }
}

// 发送心跳回包 0x00
void XCManager::SendAliveACK(int fd) {
    XCAliveACK aliveACK;
    aliveACK.set_servertime(11111111L);
    unsigned short length = aliveACK.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    aliveACK.SerializeToArray(buf, length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::ALIVE_ACK,buf);
    tcp_poll_server_send_data(fd, data, datalen);
}

// 发送登录回包 0x01
void XCManager::SendLoginACK(int fd,long long uid,int result) {
    XCLoginACK loginACK;
    loginACK.set_result(result);
    if(88888 == uid) {
        loginACK.set_ismanager(true);
    }
    else {
        loginACK.set_ismanager(false);
    }
    loginACK.set_onlineusers(999);
    unsigned short length = loginACK.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    loginACK.SerializeToArray(buf, length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::LOGIN_ACK,buf);
    tcp_poll_server_send_data(fd, data, datalen);
}
// 广播服务器关闭 0x02
void XCManager::BroadcastServerClose() {
    XCServerCloseBRO serverCloseBRO;
    serverCloseBRO.set_msg("服务器维护，关闭！");
    unsigned short length = serverCloseBRO.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    serverCloseBRO.SerializeToArray(buf, length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::SERVER_CLOSE_ACK,buf);
    tcp_poll_server_broadcast_data(serverHandler,data, datalen);
}

// 广播房间人数
void XCManager::BroadcastOnlieUserCount() {
    XCOnlineCountBRO onlineCountBRO;
    onlineCountBRO.set_count(9999);
    unsigned short length = onlineCountBRO.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    onlineCountBRO.SerializeToArray(buf, length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::USER_COUNT_ACK,buf);
    tcp_poll_server_broadcast_data(serverHandler,data, datalen);
}


void XCManager::BroadcastChatMessage(void * content) {
    if(NULL == content) {
        return ;
    }
    cout<<"============"<<endl;
    XCChatMsg * chatMsg = static_cast<XCChatMsg*>(content);
    XCChatMsgBRO chatMsgBRO;
    ChatPlayer * player = chatMsgBRO.mutable_player();

    auto chat = chatMsgBRO.mutable_chat();
    chat->set_content(chatMsg->content());
    chat->set_type(chatMsg->type());

    player->set_headimg(chatMsg->player().headimg());
    player->set_id(chatMsg->player().id());
    player->set_name(chatMsg->player().name());
    unsigned short length = chatMsgBRO.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    chatMsgBRO.SerializePartialToArray(buf,length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::CHAT_MESSAGE_ACK,buf);
    tcp_poll_server_broadcast_data(serverHandler,data,datalen);
}

// 广播消息
void XCManager::BroadcastSystemMessage() {
    XCSystemMsgBRO sysMsgBRO;
    sysMsgBRO.add_content("系统消息:好消息，好消息，明天要放假啦~");
    unsigned short length = sysMsgBRO.ByteSize();
    unsigned char* buf = new unsigned char[length];
    // 序列化
    sysMsgBRO.SerializeToArray(buf, length);
    unsigned char data[MAXLINE];
    int datalen;
    Pack(data, &datalen ,length ,ROOM_SERVER_ACK::SYSTEM_MESSAGE_ACK,buf);
    tcp_poll_server_broadcast_data(serverHandler,data, datalen);
}

