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

- `pthread`

pthread库用进入参考：[CMake引入pthread库](http://tangsanzang.tk/2019/01/02/cmakepthread/)

在不同平台，protobuf文件需重新生成，不然会报错。

执行项目中`protoc/create_proto_file.sh`脚本即可。



## 2、Android客户端

初步完成了聊天室功能，多个用户可进入聊天室，实时聊天

示例APK下载：https://github.com/IcyHeartBX/xc-chat/releases

![](http://tangsanzang.tk/file/img/2019010215292331223.png)