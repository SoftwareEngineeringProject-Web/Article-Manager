package com.example.blog.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import com.example.blog.entity.Favorite;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.FavoriteArticleService;
import com.example.blog.service.FavoriteService;
import com.example.blog.service.UserService;

@Controller
public class FavoriteController {
  @Autowired
  UserService userService;
  @Autowired
  ArticleService articleService;
  @Autowired
  FavoriteService favoriteService;
  @Autowired
  FavoriteArticleService favoriteArticleService;

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

  @PostMapping("/{username}/add-favorite/{articleId}")
  public ResponseEntity<Favorite> addFavorite(@PathVariable("username") String username, @RequestBody Map<String, String> payload) {
    String favoriteName = payload.get("name");
    User user = userService.findUserByUsername(username);
    Favorite favorite = favoriteService.addFavorite(user.getId(), favoriteName);
    return ResponseEntity.ok(favorite);
  }

  @PostMapping("/{username}/edit-favorite/{favoriteId}")
  public ResponseEntity<Favorite> editFavorite(@PathVariable("username") String username, @PathVariable("favoriteId") Long favoriteId,
                                               @RequestBody Map<String, String> payload) {
    Favorite favorite = favoriteService.getFavoriteById(favoriteId);
    if (Objects.equals(favorite.getUserId(), userService.findUserByUsername(username).getId())) {
      favorite.setName(payload.get("name"));
      favoriteService.update(favorite);
      return ResponseEntity.ok(favorite);
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }
  }

  @DeleteMapping("/{username}/delete-favorite/{favoriteId}")
  public ResponseEntity<String> deleteFavorite(@PathVariable("username") String username, @PathVariable("favoriteId") Long favoriteId) {
    if (Objects.equals(favoriteService.getFavoriteById(favoriteId).getUserId(), userService.findUserByUsername(username).getId())) {
      favoriteService.deleteFavoriteByFavoriteId(favoriteId);
      return ResponseEntity.ok("删除成功");
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
    }
  }

  @DeleteMapping("/{username}/delete-article-from-favorite/{favoriteId}/{articleId}")
  public ResponseEntity<String> deleteArticleFromFavorites(@PathVariable("username") String username, @PathVariable("favoriteId") Long favoriteId,
                                                           @PathVariable("articleId") Long articleId) {
    if (Objects.equals(favoriteService.getFavoriteById(favoriteId).getUserId(), userService.findUserByUsername(username).getId())) {

      favoriteArticleService.deleteFavoriteArticle(articleId, favoriteId);
      return ResponseEntity.ok("删除成功");
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
    }
  }

}
