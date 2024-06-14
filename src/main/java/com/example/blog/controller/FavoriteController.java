package com.example.blog.controller;

import com.example.blog.entity.Favorite;
import com.example.blog.entity.User;
import com.example.blog.service.FavoriteService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class FavoriteController {
  @Autowired
  UserService userService;
  @Autowired
  FavoriteService favoriteService;
  @PostMapping("/{username}/{id}/add-favorite")
  public ResponseEntity<Favorite> addFavorite(@PathVariable("username") String username, @PathVariable("id") Long articleId,
                                              @RequestBody Map<String, String> payload) {
    String favoriteName = payload.get("name");
    User user = userService.findUserByUsername(username);
    Favorite favorite = new Favorite(user.getId(), favoriteName);
    favoriteService.insert(favorite);
    return ResponseEntity.ok(favorite);
  }
}
