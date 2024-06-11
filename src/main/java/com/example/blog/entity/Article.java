package com.example.blog.entity;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false)
    private Integer views;

    @Column(nullable = false)
    private Integer likes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private Instant createdAt;

    public Article() {
    }

    public Article(Long id, String title, String content, User user, Category category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.category = category;
        this.createdAt = Instant.now();
        this.views = 0;
        this.likes = 0;
    }


    public Long getId() {
        return id;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer incrementViews() {
        return ++views;
    }

    public Integer incrementLikes() {
        return ++likes;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt() {
        this.createdAt = Instant.now();
    }

    public Instant getCreateTime() {
        return this.createdAt;
    }

    public Category getCategory() {
        return category;
    }


    public void setCategory(Category category) {
        this.category = category;
    }

    public String getFormattedContent() {
        return content != null ? content.replace("\n", "<br>") : null;
    }
    public String toString() {
        return "Article{id = " + id + ", title = " + title + ", content = " + content + ", user = " + user + ", category = " + category + "}";
    }

}
