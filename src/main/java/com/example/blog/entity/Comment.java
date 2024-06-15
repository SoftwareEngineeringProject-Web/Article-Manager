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

  @ManyToOne
  @JoinColumn(name = "response_id")
  private Comment responseTo;

  @Column(name = "created_at")
  private Instant createdAt;


  public Comment() {
  }

  public Comment(User user, Article article, String content, Comment responseTo) {
    this.user = user;
    this.article = article;
    this.content = content;
    this.responseTo = responseTo;
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

  public Comment getResponseTo() {
    return responseTo;
  }

  public void setCreatedAt() {
    this.createdAt = Instant.now();
  }

  public Instant getCreateTime() {
    return this.createdAt;
  }

}
