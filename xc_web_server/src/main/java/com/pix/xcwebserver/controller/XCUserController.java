package com.pix.xcwebserver.controller;

import com.pix.xcwebserver.bean.XCUser;
import com.pix.xcwebserver.service.IUserService;
import com.pix.xcwebserver.utils.ConstUtils;
import com.pix.xcwebserver.utils.RSPSUtils;
import com.pix.xcwebserver.utils.TextUtils;
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

    /**
     * 用户注册接口
     * 规则：用户输入用户邮箱，昵称，密码，必填项进行注册
     * 邮箱：
     *      检验邮箱正确性，是否存在
     * 昵称：
     *      昵称不能为空，最多32个字符，超过自动截取
     *
     * @param email 邮箱
     * @param name 名称
     * @param password 密码
     * @return
     */
    @RequestMapping("/register")
    public Map<String,Object> userRegister(String email,String name,String password) {
        int returnCode = 0;
        // 空检查
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            returnCode = -1;
            return RSPSUtils.rsp(returnCode,null,"参数错误！");
        }
        // 检测邮箱正确性

        if(null == email || !email.matches(ConstUtils.MAIL_PATTEN)) {
            returnCode = -2;
            return RSPSUtils.rsp(returnCode,null,"邮箱格式不正确!");
        }
        if(null == name || "".equals(name.trim())) {
            returnCode = -3;
            return RSPSUtils.rsp(returnCode,null,"昵称不能为空");
        }
        if(null == password || "".equals(password.trim()) || 32 != password.length()) {
            returnCode = -4;
            return RSPSUtils.rsp(returnCode,null,"密码格式不正确");
        }
        if(null != userService.hasUserByMail(email)) {
            returnCode = -5;
            return RSPSUtils.rsp(returnCode,null,"邮箱已注册!");
        }
        // 进行注册
        if(null != userService) {
            return RSPSUtils.rsp(0,userService.addUser(email,name,password),"注册成功!");
        }
        return RSPSUtils.rsp(-100,null,"服务器内部错误！");
    }

//    /**
//     * 删除所有用户
//     * @return
//     */
//    @RequestMapping("/deleteall")
//    public Map<String,Object> deleteAll() {
//        // 进行注册
//        if(null != userService && userService.deleteAllUser()) {
//            return RSPSUtils.rsp(0,null,"删除成功！");
//        }
//        return RSPSUtils.rsp(-100,null,"服务器内部错误！");
//    }

    @RequestMapping("/login")
    public Map<String,Object> login(String loginparam,String password) {
        int ret = 0;
        if(TextUtils.isEmpty(loginparam)) {
            ret = -1;
            return RSPSUtils.rsp(ret,null,"参数有误！");
        }
        XCUser user = null;
        if(loginparam.matches(ConstUtils.MAIL_PATTEN)) { // 是邮箱
            user = userService.getUserByMail(loginparam);

        }
        else if(loginparam.matches("\\d+")) {    // 是id
            int uid = Integer.parseInt(loginparam);
            user = userService.getUserById(uid);
        }
        if(null == user) {
            return RSPSUtils.rsp(-2,null,"用户不存在!");

        }
        if(password.equals(user.getPassword())) {
            return RSPSUtils.rsp(0,user,"登录成功!");
        }
        else {
            return RSPSUtils.rsp(-2,null,"密码错误!");
        }
    }

}
