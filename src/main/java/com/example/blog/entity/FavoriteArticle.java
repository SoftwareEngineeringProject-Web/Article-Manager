package com.example.blog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite_articles")
@IdClass(FavoriteArticleMultiKeys.class)
public class FavoriteArticle {
  @Id
  private Long favoriteId;

  @Id
  private Long articleId;

  public FavoriteArticle() {}
  public FavoriteArticle(Long favoriteId, Long articleId) {
    this.favoriteId = favoriteId;
    this.articleId = articleId;
  }
}
