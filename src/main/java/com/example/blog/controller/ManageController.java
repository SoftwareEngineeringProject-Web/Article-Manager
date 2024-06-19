package com.example.blog.controller;

import com.example.blog.data.FavoriteWithArticles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import com.example.blog.entity.User;
import com.example.blog.service.ManageService;
import com.example.blog.service.UserService;

import java.util.ArrayList;
import java.util.Map;


@Controller
public class ManageController {

  @Autowired
  private UserService userService;
  @Autowired
  public PasswordEncoder passwordEncoder;
  @Autowired
  private ManageService manageService;

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

  @PostMapping("/{username}/change-password")
  public String changePasswordPost(@PathVariable("username") String username, @RequestParam("password") String password) {
    userService.changePassword(username, password);
    return "redirect:/" + username + "/background";
  }

  @PostMapping("/{username}/change-information")
  public String changeInformationPost(@PathVariable("username") String username, @RequestParam("email") String email,
                                      @RequestParam("name") String name, @RequestParam("username") String newUsername) {
    userService.changeInformation(username, email, name, newUsername);
    return "redirect:/" + username + "/background";
  }

  @GetMapping("/{username}/background")
  public String managePage(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "background";
  }

  @GetMapping("/{username}/manage/manage-articles")
  public String manageArticles(@PathVariable("username") String username,
                               @RequestParam(name = "title", required = false) String title,
                               @RequestParam(name = "category", required = false) Long categoryId,
                               @RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    Map<String,Object> articlesData = manageService.setArticlesInformation(user.getId(), categoryId, title, page);
    model.addAllAttributes(articlesData);
    return "manage/manage-articles";

  }

  @GetMapping("/{username}/manage/manage-comments")
  public String manageComments(@PathVariable("username") String username,
                               @RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
    User user = userService.findUserByUsername(username);
    Map<String,Object> commentsData = manageService.setCommentsInformation(user.getId(), page);
    model.addAttribute("user", user);
    model.addAllAttributes(commentsData);
    return "manage/manage-comments";

  }

  @GetMapping("/{username}/manage/manage-favorites")
  public String manageFavorites(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = manageService.setFavoritesInformation(user.getId());
    model.addAttribute("favoriteWithArticlesList", favoriteWithArticlesList);
    return "manage/manage-favorites";
  }

  @GetMapping("/{username}/manage/change-information")
  public String changeInformation(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "manage/change-information";
  }

  @GetMapping("/{username}/manage/change-password")
  public String changePassword(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "manage/change-password";
  }

  @GetMapping("/{username}/background/{htmlPage}")
  public String loadContent(@PathVariable("username") String username,
                            @PathVariable("htmlPage") String htmlPage) {
    return "forward:/" + username + "/manage/" + htmlPage;
  }

}
