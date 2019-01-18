CREATE TABLE t_xc_user (
  uid int AUTO_INCREMENT PRIMARY KEY,
  name char(32) NOT NULL,
  email TEXT NOT NULL,
  password char(32) NOT NULL,
  level int,
  token char(32),
  sex int
);


-- id从10000开始
ALTER TABLE t_xc_user  AUTO_INCREMENT=10000;

-- 插入测试数据
INSERT INTO t_xc_user(name,email,password,level,token,sex) VALUES('pix','pixboly@gmail.com','',100,'',0);

-- 删除表
DROP TABLE t_xc_user;