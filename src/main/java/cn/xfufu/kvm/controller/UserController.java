package cn.xfufu.kvm.controller;


import cn.xfufu.kvm.entity.User;
import cn.xfufu.kvm.service.UserServiceImpl;
import cn.xfufu.kvm.util.Md5Kit;
import cn.xfufu.kvm.util.RandomKit;
import cn.xfufu.kvm.util.SendEmailKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 用户
 */
@Controller
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SendEmailKit sendEmailKit;

    @Autowired
    private RandomKit randomKit;

    @Autowired
    private Md5Kit md5Kit;

    @RequestMapping("/login")
    @ResponseBody
    public String login(String username, String password, HttpSession session) {

        User user = userService.findUserByEmail(username);
        if (user == null) {
            return "-2";
        } else if (md5Kit.toMd5(password).equals(user.getPassword())) {
            session.setAttribute("user", user);
            return "0";
        } else {
            return "-1";
        }
    }

    @RequestMapping("/sendEmail")
    @ResponseBody
    public String sendEmail(User user) {
        String code = randomKit.randomCode();
        if (sendEmailKit.sendHTMLEmail(user.getEmail(), "注册用户：" + user.getEmail(), code)) {
            return code;
        }
        return null;
    }

    @RequestMapping("/saveUser")
    @ResponseBody
    public Boolean saveUser(User user) {
        return userService.saveUser(user);
    }
}
