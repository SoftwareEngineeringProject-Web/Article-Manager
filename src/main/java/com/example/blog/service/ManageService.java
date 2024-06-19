package com.example.blog.service;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Favorite;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.FavoriteArticleRepository;
import com.example.blog.repository.FavoriteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

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

  public void setArticlesInformation(Long userId, Long categoryId, String title, Integer page, Model model) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Article> articlePages;
    List<Category> categories = categoryRepository.findByUserId(userId);

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

    model.addAttribute("articles", articlePages.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", articlePages.getTotalPages());
    model.addAttribute("categories", categories);
  }

  public void setCommentsInformation(Long userId, Integer page, Model model) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Comment> commentPages = commentRepository.findPagedByUserId(userId, pageable);

    model.addAttribute("comments", commentPages.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", commentPages.getTotalPages());
  }

  public void setFavoritesInformation(Long userId, Model model) {
    List<Favorite> favorites = favoriteRepository.findByUserId(userId);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
    favorites.forEach((favorite) -> {
      favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
        favoriteArticleRepository.findArticlesByFavoriteId(favorite.getId())));
    });
    model.addAttribute("favoriteWithArticlesList", favoriteWithArticlesList);
  }

  public Category getCategoryById(Long id) {
    return id == null ? null : categoryRepository.findById(id).orElse(null);
  }

  public List<Category> findByUserId(Long userId) {
    return categoryRepository.findByUserId(userId);
  }

  public List<Category> findByParentId(Long parentId) {
    return categoryRepository.findByParentId(parentId);
  }

  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }
}
