package com.example.blog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @JoinColumn(name = "article_id", nullable = false)
  private Long articleId;

  @Column(nullable = false)
  @Lob
  private String content;

  @Column(name = "created_at")
  private Instant createdAt;


  public Comment() {
  }

  public Comment(User user, Long articleId, String content) {
    this.user = user;
    this.articleId = articleId;
    this.content = content;
    this.setCreatedAt();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public Long getArticleId() {
    return articleId;
  }

  public void setArticleId(Long articleId) {
    this.articleId = articleId;
  }

  public String getContent() {
    return content;
  }

  public void setCreatedAt() {
    this.createdAt = Instant.now();
  }

  public Instant getCreateTime() {
    return this.createdAt;
  }

}
