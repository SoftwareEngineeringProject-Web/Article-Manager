package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private CategoryRepository categoryRepository;

  public List<Article> getAllArticles() {
    return articleRepository.findAll();
  }

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

  public List<Article> getArticlesByCategory(Category category) {
    return articleRepository.findByCategory(category);
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

  public void updateLikesById(Long id, Integer moreLikes) {
    articleRepository.updateLikesById(id, moreLikes);
  }

  public void updateLikes(Article article) {
    articleRepository.updateLikes(article.getId(), article.getLikes());
  }



  public void updateFavoritesById(Long articleId, Integer moreFavorites) {
    articleRepository.updateFavoritesById(articleId, moreFavorites);
  }

  public void setCategoryIdtoNullByCategoryId(Long categoryId) {
    articleRepository.setCategoryIdToNullByCategoryId(categoryId);
  }

  public void deleteById(Long id) {
    articleRepository.deleteById(id);
  }

  public Page<Article> getArticlesByTitleAndCategoryPaged(Long userId, String title, Category category, Pageable pageable) {
    return articleRepository.findByUserIdAndTitleAndCategory(userId, title, category, pageable);
  }

  public Page<Article> getArticlesByTitlePaged(Long userId, String title, Pageable pageable) {
    return articleRepository.findByUserIdAndTitle(userId, title, pageable);
  }

  public Page<Article> getArticlesByCategoryPaged(Long userId, Category category, Pageable pageable) {
    return articleRepository.findByUserIdAndCategory(userId, category, pageable);
  }



}
