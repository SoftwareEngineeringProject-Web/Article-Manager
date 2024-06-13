package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class ManageController {

  @Autowired
  private UserService userService;
  @Autowired
  public PasswordEncoder passwordEncoder;
  @Autowired
  private ArticleService articleService;
  @Autowired
  private CategoryService categoryService;

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
  public String changePasswordPost(@PathVariable("username") String username,@RequestParam("password") String password) {
    User user = userService.findUserByUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    userService.updateUser(user);
    return "redirect:/"+username+"/home";
  }

  @PostMapping("/{username}/change-information")
  public String changeInformationPost(@PathVariable("username") String username, @RequestParam("email") String email,
                                      @RequestParam("name") String name, @RequestParam("username") String newUsername) {
    User user = userService.findUserByUsername(username);
    user.setEmail(email);
    user.setUsername(newUsername);
    user.setName(name);
    userService.updateUser(user);
    return "redirect:/"+user.getUsername() +"/home";
  }

  @GetMapping("/{username}/background")
  public String managePage(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "background";
  }

  @GetMapping("/{username}/background/{htmlPage}")
  public String loadContent(@PathVariable("username") String username,
                            @PathVariable("htmlPage") String htmlPage,
                            @RequestParam(name = "title", required = false) String title,
                            @RequestParam(name = "category", required = false) Long categoryId,
                            @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
    User user = userService.findUserByUsername(username);
    Long userId = user.getId();
    model.addAttribute("user", user);

    switch (htmlPage) {
      case "manage-articles":
        Pageable pageable = PageRequest.of(page, 20); // 每页显示20篇标题
        Page<Article> articlePages;
        List<Category> categories = categoryService.findByUserId(user.getId());

        if (title != null && !title.isEmpty() && categoryId != null) {
          Category category = categoryService.getCategoryById(categoryId);
          articlePages = articleService.getArticlesByTitleAndCategoryPaged(userId, title, category, pageable);
        } else if (title != null && !title.isEmpty()) {
          articlePages = articleService.getArticlesByTitlePaged(userId, title, pageable);
        } else if (categoryId != null) {
          Category category = categoryService.getCategoryById(categoryId);
          articlePages = articleService.getArticlesByCategoryPaged(userId, category, pageable);
        } else {
          articlePages = articleService.getArticlesByUserIdPaged(userId, pageable);
        }

        model.addAttribute("articles", articlePages.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePages.getTotalPages());
        model.addAttribute("categories", categories);
        return "fragments/manage-articles";
      case "change-information":
        return "fragments/change-information";
      case "change-password":
        return "fragments/change-password";
      default:
        return "access-denied";
    }
  }
}
