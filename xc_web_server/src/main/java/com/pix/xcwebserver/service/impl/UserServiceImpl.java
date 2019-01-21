package com.pix.xcwebserver.service.impl;

import com.pix.xcwebserver.bean.XCUser;
import com.pix.xcwebserver.dao.UserRepository;
import com.pix.xcwebserver.service.IUserService;
import com.pix.xcwebserver.utils.ConstUtils;
import com.pix.xcwebserver.utils.RSPSUtils;
import com.pix.xcwebserver.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl() {
    }

    @Override
    public List<XCUser> getAllUser() {
        if(null != userRepository) {
            List<XCUser> users = userRepository.findAll();
            return users;
        }
        return null;
    }

    @Override
    public XCUser addUser(String email, String name, String password) {
        if(null != userRepository) {
            XCUser user = new XCUser();
            user.setEmail(email);
            user.setName(name);
            user.setPassword(password);
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public boolean deleteAllUser() {
        if(null != userRepository) {
            userRepository.deleteAll();
            userRepository.deleteAllInBatch();
            return true;
        }
        return false;
    }



    @Override
    public XCUser getUserByMail(String email) {
        if(TextUtils.isEmpty(email) || !email.matches(ConstUtils.MAIL_PATTEN)) {
            return null;
        }
        if(null != userRepository) {
            return  userRepository.findByEmail(email);

        }
        return null;
    }

    @Override
    public XCUser getUserById(int uid) {
        if(uid <= 0) {
            return null;
        }
        if(null != userRepository) {
            userRepository.findById(uid);
        }
        return null;
    }

    @Override
    public XCUser hasUserByMail(String email) {
        if(TextUtils.isEmpty(email) || !email.matches(ConstUtils.MAIL_PATTEN)) {
            return null;
        }
        // 检测用户邮箱是否存在
        List<XCUser> users = userRepository.findAll();
        for (XCUser user:users) {
            if(email.equals(user.getEmail())) {
                return user;
            }
        }
        return null;
    }
}
