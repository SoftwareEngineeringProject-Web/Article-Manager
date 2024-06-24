package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class ArticleController {
  @Autowired
  private UserService userService;
  @Autowired
  private LikeService likeService;
  @Autowired
  private ArticleService articleService;
  @Autowired
  private CategoryService categoryService;
  @Autowired
  private FavoriteArticleService favoriteArticleService;
  @Autowired
  public PasswordEncoder passwordEncoder;

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

  @GetMapping("/{username}/article/{id}")
  public String article(@PathVariable("username") String username, @PathVariable("id") Long articleId, Model model) {
    User user = userService.findUserByUsername(username);
    Article article = articleService.getArticleById(articleId);

    // 检查用户要访问的文章是否为自己的文章，或者是否为共享
    if (!Objects.equals(article.getUser().getId(), user.getId()) && !article.isPublic() && !username.equals("Admin")) {
      return "redirect:/" + username + "/access-denied";
    }
    Map<String, Object> articleData = articleService.getArticleData(user.getId(), article);
    model.addAttribute("user", user);
    model.addAllAttributes(articleData);
    return "article";
  }

  @GetMapping("/{username}/create-article")
  public String createArticle(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    List<Category> categories = categoryService.findByUserId(user.getId());

    model.addAttribute("user", user);
    model.addAttribute("categories", categories);
    return "create-article";
  }

  @PostMapping("/{username}/create-article")
  public String createArticlePost(@ModelAttribute Article article, @PathVariable("username") String username,
                                  @RequestParam("category") Long categoryId, @RequestParam(name = "isPublic") Boolean isPublic) {
    articleService.saveArticle(article, username, categoryId, isPublic);
    return "redirect:/" + username + "/home";
  }

  @GetMapping("/{username}/edit-article/{id}")
  public String editArticleForm(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
    Article article = articleService.getArticleById(id);
    User user = userService.findUserByUsername(username);

    if (article.getUser().getId() != user.getId()) {
      return "redirect:/" + username + "/access-denied";
    }

    List<Category> categories = categoryService.findByUserId(user.getId());
    model.addAttribute("article", article);
    model.addAttribute("categories", categories);
    model.addAttribute("user", user);
    return "edit-article";
  }

  @PostMapping("/{username}/edit-article/{id}")
  public String editArticle(@ModelAttribute Article article, @PathVariable("username") String username, @PathVariable("id") Long articleId,
                            @RequestParam("category") Long categoryId, @RequestParam(name = "isPublic") Boolean isPublic) {
    User user = userService.findUserByUsername(username);
    if (!Objects.equals(articleService.getArticleById(articleId).getUser().getId(), user.getId())) {
      return "redirect:/" + username + "/access-denied";
    }
    articleService.updateArticle(article, user, categoryId, isPublic);
    return "redirect:/" + username + "/background";
  }

  @DeleteMapping("/{username}/delete-article/{id}")
  public ResponseEntity<String> deleteArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId) {
    Article article = articleService.getArticleById(articleId);
    if (!article.getUser().getUsername().equals(username) && !username.equals("Admin")) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
    }
    articleService.deleteById(articleId);
    return ResponseEntity.ok("删除成功");
  }

  @GetMapping("/{username}/{id}/like")
  public ResponseEntity<Map<String, Object>> likeArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId) {
    Integer likes = likeService.getLikeCountByArticleId(articleId, username);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("likes", likes);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/{username}/{id}/favorite")
  public ResponseEntity<Map<String, Object>> favoriteArticle(@PathVariable("id") Long articleId, @RequestBody Map<String, Long> payload) {
    Long favoriteId = payload.get("favoriteId");
    Map<String, Object> response = new HashMap<>();
    Integer favoriteCount = favoriteArticleService.addFavoriteArticle(favoriteId, articleId);
    if (favoriteCount == null) {
      response.put("success", false);
      response.put("favoriteCount", 0);
    } else {
      response.put("success", true);
      response.put("favoriteCount", favoriteCount);
    }
    return ResponseEntity.ok(response);
  }
}
