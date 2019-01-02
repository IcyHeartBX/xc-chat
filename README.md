> 聊天程序

# xc-chat

`xc-chat`是我自己练习项目。

开发一个聊天Demo，包含服务端和客户端。

- `xc_server_clion`:`C++`项目写的服务端，用`CLion IDE`创建的项目。
- `XCAndroid`:是用Android写的聊天室客户端

## 1、服务器端

服务器端口:7777

编译项目`xc_server_clion`，需引入动态库，参考:[cmake引入库](http://tangsanzang.tk/2018/12/27/clioncmakelinklib/)

- `protobuf`
- `hiredis`

## 2、Android客户端

初步完成了聊天室功能，多个用户可进入聊天室，实时聊天

![](http://tangsanzang.tk/file/img/2019010215264823393.png)