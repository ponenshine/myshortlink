package org.enshine.myshortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExceptionPageController {
    /**
     * 短链接不存在跳转页面
     */
    @RequestMapping("/page/notfound")
    public String notfound() {
        return "notfound";
    }

    /**
     * error页面
     */
    @RequestMapping("/page/error")
    public String error(){
        return "error";
    }
}
