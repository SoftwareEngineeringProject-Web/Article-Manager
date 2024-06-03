package com.example.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArticleController {

    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // 返回登录页面的视图名称
    }

}
