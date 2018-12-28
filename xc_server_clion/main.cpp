#include <iostream>
#include <pthread.h>
#include "manager/XCManager.h"
using namespace std;

pthread_t t1;

/**
 * 显示服务器菜单
 */
void showMenu() {
    printf("======================================\n");
    printf("1、广播服务器关闭\n");
    printf("2、广播房间人数\n");
    printf("3、广播系统消息\n");
    printf("0、退出 \n");
    printf("======================================\n");
}
// 线程函数
void * server_thread(void * args) {
    XCManager::GetInstance()->StartServer();
    return NULL;
}

// 启动服务器，在子线程中进行
void start_server() {
    pthread_create(&t1,NULL,server_thread,NULL);   // 创建缺省线程
}

int main() {
   std::cout << "XC Service Run!\n";
    start_server();
    int option = 0;
    for(;;){
        showMenu();
        cin>>option;
        switch (option) {
            case 1:
                XCManager::GetInstance()->BroadcastServerClose();
                break;
            case 2:
                XCManager::GetInstance()->BroadcastOnlieUserCount();
                break;
            case 3:
                XCManager::GetInstance()->BroadcastSystemMessage();
                break;
            case 0:
                goto END;
        }
    }
    END:
    XCManager::GetInstance()->CloseServer();
    return 0;
}