package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.AdminService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class AdminController {

  @Autowired
  private UserService userService;
  @Autowired
  private AdminService adminService;


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

  @GetMapping("{username}/admin-management")
  public String managePage(Model model) {
    User user = userService.findUserByUsername("Admin");
    model.addAttribute("user", user);
    return "admin-management";
  }

  @GetMapping("/{username}/admin-manage/manage-articles")
  public String manageArticles(@PathVariable("username") String username,
                               @RequestParam(name = "title", required = false) String title,
                               @RequestParam(name = "category", required = false) Long categoryId,
                               @RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    Map<String, Object> articlesData = adminService.setAllArticlesInformation(title, page);
    model.addAllAttributes(articlesData);
    return "manage/admin-manage-articles";

  }

  @GetMapping("/{username}/admin-manage/manage-comments")
  public String manageComments(@PathVariable("username") String username,
                               @RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
    User user = userService.findUserByUsername(username);
    Map<String, Object> commentsData = adminService.setAllCommentsInformation(page);
    model.addAttribute("user", user);
    model.addAllAttributes(commentsData);
    return "manage/admin-manage-comments";
  }


  @GetMapping("/{username}/admin-manage/change-password")
  public String changePassword(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "manage/change-password";
  }

  @GetMapping("/{username}/admin-manage/manage-users")
  public String manageUsers(@PathVariable("username") String username,
                            @RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
    Map<String, Object> usersData = userService.findAllUsers(page);
    model.addAllAttributes(usersData);
    return "manage/manage-users";
  }

  @GetMapping("/{username}/admin-management/{htmlPage}")
  public String loadContent(@PathVariable("htmlPage") String htmlPage, @PathVariable("username") String username) {
    return "forward:/" + username + "/admin-manage/" + htmlPage;
  }

}
