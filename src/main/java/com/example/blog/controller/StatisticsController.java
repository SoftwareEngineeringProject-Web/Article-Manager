package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.StatisticsService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class StatisticsController {
  @Autowired
  private UserService userService;

  @Autowired
  private ArticleService articleService;

  @Autowired
  private CategoryService categoryService;
  @Autowired
  private StatisticsService statisticsService;

  @ModelAttribute
  public void checkUser(@PathVariable("username") String username) throws ModelAndViewDefiningException {
    // 获取当前登录用户的用户名
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUsername = authentication.getName();

    // 如果当前登录用户与要访问的用户不匹配，则进行相应处理
    if (!loggedInUsername.equals(username)) {
      String redirectUrl = "/" + loggedInUsername + "/home";
      RedirectView redirectView = new RedirectView(redirectUrl);
      throw new ModelAndViewDefiningException(new ModelAndView(redirectView));
    }
  }

  @GetMapping("{username}/statistics")
  public String getStatistics(Model model, @PathVariable String username) {
    // 获取当前登录用户
    User user = userService.findUserByUsername(username);
    statisticsService.getAllStatistics(user, model);
    return "statistics";
  }
}
