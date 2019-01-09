cmake_minimum_required(VERSION 3.8)
project(xc_server_clion)

set(CMAKE_CXX_STANDARD 11)

add_executable(xc_server_clion
        main.cpp
        tcp/tcp_poll_server.c
        tcp/tcp_poll_server.h
        protoc/xc_protoc.pb.h
        protoc/xc_protoc.pb.cc
        manager/XCManager.cpp
        manager/XCManager.h
        utils/NetUtils.h
        utils/PackConsts.h
        utils/PackUtils.h
        consts/RedisConsts.h
        services/IOnlineUserService.h
        define/BeanDefine.h
        services/impl/OnlineUserService.cpp
        services/impl/OnlineUserService.h
        services/IRoomService.h
        services/impl/XCRoomService.cpp
        services/impl/XCRoomService.h
        utils/TypeUtils.h)
include_directories(/usr/local/hiredis/)
include_directories(/usr/local/protobuf/include)
link_directories(/usr/local/hiredis/)
link_directories(/usr/local/protobuf/lib/)

target_link_libraries(xc_server_clion /usr/local/hiredis/libhiredis.a)
target_link_libraries(xc_server_clion /usr/local/protobuf/lib/libprotobuf.a)

find_package(Threads)
target_link_libraries(xc_server_clion ${CMAKE_THREAD_LIBS_INIT})