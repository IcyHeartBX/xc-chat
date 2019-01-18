package com.pix.xcwebserver.service;

import com.pix.xcwebserver.bean.XCUser;

import java.util.List;

/**
 * 用户服务来
 */
public interface IUserService {
    /**
     * 取得所有用户
     * @return
     */
    public List<XCUser> getAllUser();

    /**
     * 增加一个用户
     * @param email
     * @param name
     * @param password
     * @return 如果正确返回新增用户信息，如果失败，则返回null
     */
    public XCUser addUser(String email,String name,String password);

    /**
     * 删除所有用户，测试用
     * @return
     */
    public boolean deleteAllUser();
}
