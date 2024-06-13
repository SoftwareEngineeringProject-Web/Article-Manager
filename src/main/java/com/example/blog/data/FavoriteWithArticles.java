package com.example.blog.data;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.service.FavoriteService;

public class FavoriteWithArticles {
  private Favorite favorite;
  private List<Article> articles;

  @Autowired
  static private FavoriteService favoriteService;

  public FavoriteWithArticles(Favorite favorite, List<Article> articles) {
    this.favorite = favorite;
    this.articles = articles;
  }

  public Favorite getFavorite() {
    return favorite;
  }

  public List<Article> getArticles() {
    return articles;
  }

  public static List<FavoriteWithArticles> getAllByUserId(Long userId) {
    List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
    favorites.forEach((favorite) -> {
      favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
                                   favoriteService.getArticlesByFavoriteId(favorite.getId())));
    });
    return favoriteWithArticlesList;
  }
}
