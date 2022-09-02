package cn.xfufu.kvm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/")
    public String index() {
        return "login";
    }

    @RequestMapping("/main")
    public String main() {
        return "main";
    }

    @RequestMapping("/add")
    public String add() {
        System.out.println("xxx");
        return "add";
    }


    @RequestMapping("/regist")
    public String regist() {
        return "regist";
    }

}
