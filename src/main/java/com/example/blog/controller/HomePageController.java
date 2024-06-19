package com.example.blog.controller;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.FavoriteArticleService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class HomePageController {
  @Autowired
  private UserService userService;
  @Autowired
  private ArticleService articleService;
  @Autowired
  private FavoriteArticleService favoriteArticleService;

  @GetMapping("/{username}/home")
  public String userHome(@PathVariable("username") String username, @RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {
    User user = userService.findUserByUsername(username);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = favoriteArticleService.getFavoriteArticleListByUserId(user.getId());

    // 分页处理
    Pageable pageable = PageRequest.of(page, 10); // 每页显示10篇文章
    Page<Article> articlePage = articleService.getArticlesByUserIdPaged(user.getId(), pageable);

    model.addAttribute("user", user);
    model.addAttribute("articles", articlePage.getContent());
    model.addAttribute("favoriteWithArticlesList", favoriteWithArticlesList);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", articlePage.getTotalPages());
    return "home";
  }
}
