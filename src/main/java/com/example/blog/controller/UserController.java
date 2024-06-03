package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 登录页面请求处理方法
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // 返回登录页面的视图名称
    }


    // 注销请求处理方法
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // 返回注册页面的视图名称
    }

    // 注册请求处理方法
    @PostMapping("/register")
    public String register(@ModelAttribute User user,  @RequestParam String username, HttpSession session) {

        User findUser = userService.findUserByUsername(username);

        if(findUser!= null){
            // 用户名已存在，返回注册页面并显示错误消息
            return "redirect:/register?error=true";
        }

        else{
            userService.registerUser(user); // 保存用户信息到数据库
            return "redirect:/login"; // 重定向到登录页面
        }
    }
}
