package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // 返回登录页面的视图名称
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = userService.findUserByUsername(username);
        if (user != null && userService.passwordEncoder.matches(password, user.getPassword())) {
            // 登录成功，将用户信息存储到会话中
            session.setAttribute("user", user);
            return "redirect:/home"; // 重定向到用户首页
        } else {
            // 登录失败，返回登录页面并显示错误消息
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // 返回注册页面的视图名称
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        // 在这里实现用户注册逻辑，例如验证用户信息、创建新用户等
        // 如果注册成功，可以重定向到登录页面或其他页面
        // 如果注册失败，可以返回注册页面并给出错误提示
        userService.registerUser(user); // 保存用户信息到数据库
        return "redirect:/login"; // 重定向到登录页面
    }



}
