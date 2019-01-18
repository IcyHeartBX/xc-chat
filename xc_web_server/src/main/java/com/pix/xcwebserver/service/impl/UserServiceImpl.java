package com.pix.xcwebserver.service.impl;

import com.pix.xcwebserver.bean.XCUser;
import com.pix.xcwebserver.dao.UserRepository;
import com.pix.xcwebserver.service.IUserService;
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
}
