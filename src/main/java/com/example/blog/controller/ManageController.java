package com.example.blog.controller;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.*;
import com.example.blog.service.*;
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

import java.util.ArrayList;
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
  @Autowired
  private CommentService commentService;
  @Autowired
  private FavoriteService favoriteService;

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
    User user = userService.findUserByUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    userService.updateUser(user);
    return "redirect:/" + username + "/background";
  }

  @PostMapping("/{username}/change-information")
  public String changeInformationPost(@PathVariable("username") String username, @RequestParam("email") String email,
                                      @RequestParam("name") String name, @RequestParam("username") String newUsername) {
    User user = userService.findUserByUsername(username);
    user.setEmail(email);
    user.setUsername(newUsername);
    user.setName(name);
    userService.updateUser(user);
    return "redirect:/" + user.getUsername() + "/background";
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
    Pageable pageable = PageRequest.of(page, 15);

    switch (htmlPage) {
      case "manage-articles":
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
        return "manage/manage-articles";
      case "manage-comments":
        Page<Comment> commentPages = commentService.getCommentsByUserIdPaged(userId, pageable);
        model.addAttribute("comments", commentPages.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPages.getTotalPages());
        return "manage/manage-comments";
      case "manage-favorites":
        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
        favorites.forEach((favorite) -> {
          favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
              favoriteService.getArticlesByFavoriteId(favorite.getId())));
        });
        model.addAttribute("favoriteWithArticlesList", favoriteWithArticlesList);
        return "manage/manage-favorites";
      case "change-information":
        return "manage/change-information";
      case "change-password":
        return "manage/change-password";
      default:
        return "access-denied";
    }
  }
}
