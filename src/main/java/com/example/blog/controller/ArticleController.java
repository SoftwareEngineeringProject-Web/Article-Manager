package com.example.blog.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArticleController {

    @GetMapping("/home")
    public String showHomePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("name", userDetails.getUsername());
        return "home"; // 返回登录页面的视图名称
    }

}
