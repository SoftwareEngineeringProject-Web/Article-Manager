package com.example.blog.entity.relation;

import com.example.blog.multikeys.FavoriteArticleMultiKeys;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_articles")
@IdClass(FavoriteArticleMultiKeys.class)
public class FavoriteArticle {
  @Id
  private Long favoriteId;

  @Id
  private Long articleId;

  public Long getFavoriteId() {
    return favoriteId;
  }

  public void setFavoriteId(Long favoriteId) {
    this.favoriteId = favoriteId;
  }

  public Long getArticleId() {
    return articleId;
  }

  public void setArticleId(Long articleId) {
    this.articleId = articleId;
  }

  public FavoriteArticle() {
  }

  public FavoriteArticle(Long favoriteId, Long articleId) {
    this.favoriteId = favoriteId;
    this.articleId = articleId;
  }

}
