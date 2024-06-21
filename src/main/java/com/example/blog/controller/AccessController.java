package com.example.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccessController {

  @GetMapping("/{username}/access-denied")
  public String accessDeniedPage(@PathVariable("username") String username, Model model) {
    model.addAttribute("username", username);
    return "access-denied";
  }

}
