package com.example.blog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorite {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String name;

  public Favorite() {
  }

  public Favorite(Long userId, String name) {
    this.userId = userId;
    this.name = name;
  }

  public Favorite(Long id, Long userId, String name) {
    this.id = id;
    this.userId = userId;
    this.name = name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
