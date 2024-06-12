package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccessController {

  @Autowired
  private UserService userService;

  @GetMapping("/{username}/access-denied")
  public String accessDeniedPage(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "access-denied";
  }

}
