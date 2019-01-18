package com.pix.xcwebserver.service.impl;

import com.pix.xcwebserver.bean.XCUser;
import com.pix.xcwebserver.dao.UserRepository;
import com.pix.xcwebserver.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
