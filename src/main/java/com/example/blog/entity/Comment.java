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

  @ManyToOne
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @Column(nullable = false)
  @Lob
  private String content;

  @Column(name = "created_at")
  private Instant createdAt;


  public Comment() {
  }

  public Comment(User user, Article article, String content) {
    this.user = user;
    this.article = article;
    this.content = content;
    this.setCreatedAt();
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public Article getArticle() {
    return article;
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

  public String getFormattedContent() {
    return content != null ? content.replace("\n", "<br>") : null;
  }

}
