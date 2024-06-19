package com.example.blog.service;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Favorite;
import com.example.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManageService {

  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private FavoriteArticleRepository favoriteArticleRepository;

  public Map<String, Object> setArticlesInformation(Long userId, Long categoryId, String title, Integer page) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Article> articlePages;
    List<Category> categories = categoryRepository.findByUserId(userId);
    Map<String, Object> articlesData = new HashMap<>();

    if (title != null && !title.isEmpty() && categoryId != null) {
      Category category = categoryRepository.findById(categoryId).orElse(null);
      articlePages = articleRepository.findByUserIdAndTitleAndCategory(userId, title, category, pageable);
    } else if (title != null && !title.isEmpty()) {
      articlePages = articleRepository.findByUserIdAndTitle(userId, title, pageable);
    } else if (categoryId != null) {
      Category category = categoryRepository.findById(categoryId).orElse(null);
      articlePages = articleRepository.findByUserIdAndCategory(userId, category, pageable);
    } else {
      articlePages = articleRepository.findPagedByUserId(userId, pageable);
    }

    articlesData.put("articles", articlePages.getContent());
    articlesData.put("currentPage", page);
    articlesData.put("totalPages", articlePages.getTotalPages());
    articlesData.put("categories", categories);
    return articlesData;
  }

  public Map<String, Object> setCommentsInformation(Long userId, Integer page) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Comment> commentPages = commentRepository.findPagedByUserId(userId, pageable);
    Map<String, Object> commentsData = new HashMap<>();
    commentsData.put("comments", commentPages.getContent());
    commentsData.put("currentPage", page);
    commentsData.put("totalPages", commentPages.getTotalPages());
    return commentsData;
  }

  public ArrayList<FavoriteWithArticles> setFavoritesInformation(Long userId) {
    List<Favorite> favorites = favoriteRepository.findByUserId(userId);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
    favorites.forEach((favorite) -> {
      favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
        favoriteArticleRepository.findArticlesByFavoriteId(favorite.getId())));
    });
    return favoriteWithArticlesList;
  }
}
