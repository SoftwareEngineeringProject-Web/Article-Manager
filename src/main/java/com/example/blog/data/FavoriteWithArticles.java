package com.example.blog.data;

import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;

import java.util.List;

public class FavoriteWithArticles {
  private Favorite favorite;
  private List<Article> articles;


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

}
