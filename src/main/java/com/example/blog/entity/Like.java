package com.example.blog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
@IdClass(LikeMultiKeys.class)
public class Like {
  @Id
  private Long userId;

  @Id
  private Long articleId;

  public Like() {
  }

  public Like(Long userId, Long articleId) {
    this.userId = userId;
    this.articleId = articleId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getArticleId() {
    return articleId;
  }

  public void setArticleId(Long articleId) {
    this.articleId = articleId;
  }

}
