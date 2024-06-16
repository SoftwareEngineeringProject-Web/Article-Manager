package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SearchController {
  @Autowired
  private UserService userService;
  @GetMapping("/{username}/search-articles")
  public String showSearchArticlesPage(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "search-articles";
  }

  @PostMapping("/{username}/search-articles")
  public String searchArticles(String query) {
    // TODO: 实现文章搜索功能
    return "search-articles";
  }
}
