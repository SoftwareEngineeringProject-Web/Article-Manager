package com.example.blog.service;

import com.example.blog.entity.*;
import com.example.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleService {
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private FavoriteArticleRepository favoriteArticleRepository;

  public Article getArticleById(Long articleId) {
    return articleRepository.findById(articleId).orElse(null);
  }

  public void saveArticle(Article article, String username, Long categoryId, boolean isPublic) {
    User user = userRepository.findByUsername(username);
    Category category = categoryRepository.findById(categoryId).orElse(null);
    article.setCategory(category);
    article.setUser(user);
    article.setCreatedAt();
    article.setPublic(isPublic);
    article.setLikes(0);
    article.setViews(0);
    article.setFavorites(0);
    article.setComments(0);
    articleRepository.save(article);
  }

  public Page<Article> getArticlesByUserIdPaged(Long userId, Pageable pageable) {
    return articleRepository.findPagedByUserId(userId, pageable);
  }

  public void updateArticle(Article article, User user, Long categoryId, boolean isPublic) {
    Category category = categoryRepository.findById(categoryId).orElse(null);
    article.setCategory(category);
    article.setUser(user);
    article.setPublic(isPublic);
    articleRepository.updateArticle(article.getTitle(), article.getContent(), article.getCategory(), article.isPublic(), article.getId());
  }

  public void updateViews(Article article) {
    articleRepository.updateViews(article.getId(), article.getViews());
  }

  public void deleteById(Long id) {
    articleRepository.deleteById(id);
  }

  public Map<String, Object> getArticleData(Long userId, Article article) {
    Map<String, Object> data = new HashMap<>();
    int favoriteCount = favoriteArticleRepository.countByArticleId(article.getId());
    Category category = article.getCategory() == null ? null : categoryRepository.findById(article.getCategory().getId()).orElse(null);
    String categoryPath = category == null ? null : category.getFullCategoryPath();
    List<Favorite> favoriteList = favoriteRepository.findByUserId(userId);
    article.incrementViews();
    updateViews(article);
    List<Comment> comments = commentRepository.findByArticleId(article.getId());

    data.put("article", article);
    data.put("categoryPath", categoryPath);
    data.put("favoriteList", favoriteList);
    data.put("comments", comments);
    data.put("favoriteCount", favoriteCount);
    return data;
  }
}
