package com.pix.xcwebserver.controller;

import com.pix.xcwebserver.bean.XCUser;
import com.pix.xcwebserver.dao.UserRepository;
import com.pix.xcwebserver.factory.XCUserServiceFractory;
import com.pix.xcwebserver.service.IUserService;
import com.pix.xcwebserver.service.impl.UserServiceImpl;
import com.pix.xcwebserver.utils.RSPSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Controller
public class XCUserController {
    // 注入userService
    @Resource(name = "userService")
    private IUserService userService;

    @RequestMapping("/xcusers")
    public @ResponseBody
    Map<String,Object> getJpaEmp() {
        List<XCUser> emps = userService.getAllUser();
//        List<XCUser> emps = userRepository.findAll();
        Map<String,List<XCUser>> list = new HashMap<>();
        list.put("list",emps);
        return RSPSUtils.rsp(0,list,"jpa emp表");
    }

    @RequestMapping("/hello")
    public String index() {
        return "Hello SprintBoot 22222";
    }
}
