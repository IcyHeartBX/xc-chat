# xc_web_server
聊天服务器web服务器
## 1、创建工程
工程名:`xc_web_server`

包名:`com.pix.xcwebserver`

## 2、增加用户controller
`controller/XCUserController`

## 3、在`mysql`中创建用户表
参考:附录1

## 4、创建`bean`类
用户bean:`XCUser`
- id主键注解：https://blog.csdn.net/heatdeath/article/details/79841171


## 5、创建持久层`dao`接口
`UserRepository`
```java
package com.pix.xcwebserver.dao;

import com.pix.xcwebserver.bean.XCUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<XCUser,Integer> {
}

```
## 6、创建业务层`service`
`IUserServcie`

## 7、控制层示例
`XCUserController`

## 
# 附录
## `sql创建脚本`
### 1、用户表脚本
```sql
   -- 删除表
   DROP TABLE t_xc_user;
   
   CREATE TABLE t_xc_user (
     uid int AUTO_INCREMENT PRIMARY KEY,
     name char(32) NOT NULL,
     email TEXT NOT NULL,
     password char(32) NOT NULL,
     level int,
     token char(32),
     sex int
   );
   ALTER TABLE t_xc_user  AUTO_INCREMENT=10000;
   
   -- 插入测试数据
   INSERT INTO t_xc_user(name,email,password,level,token,sex) VALUES('pix','pixboly@gmail.com','',100,'',0);
'');
```


# 参考
https://blog.csdn.net/zhizhuodewo6/article/details/81365516
