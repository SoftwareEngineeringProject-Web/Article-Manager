package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private CategoryService categoryService;

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
  public String register(@ModelAttribute User user, @RequestParam String username) {

    User findUser = userService.findUserByUsername(username);

    if (findUser != null) {
      // 用户名已存在，返回注册页面并显示错误消息
      return "redirect:/register?error=true";
    } else {
      userService.registerUser(user); // 保存用户信息到数据库
      return "redirect:/login"; // 重定向到登录页面
    }
  }

  @DeleteMapping("/{username}/delete-user/{userId}")
  public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId, @PathVariable("username") String username) {
    if (username.equals("Admin")) {
      categoryService.deleteAllCategoryByUserId(userId);
      userService.deleteUser(userId);
      return ResponseEntity.ok("删除成功");
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
    }
  }

  @PostMapping("/{username}/change-password")
  public String changePasswordPost(@PathVariable("username") String username, @RequestParam("password") String password) {
    userService.changePassword(username, password);
    if (username.equals("Admin")) {
      return "redirect:/" + username + "/admin-management";
    }
    return "redirect:/" + username + "/background";
  }

  @PostMapping("/{username}/change-information")
  public String changeInformationPost(@PathVariable("username") String username, @RequestParam("email") String email,
                                      @RequestParam("name") String name, @RequestParam("username") String newUsername) {
    userService.changeInformation(username, email, name, newUsername);
    return "redirect:/" + username + "/background";
  }
}
