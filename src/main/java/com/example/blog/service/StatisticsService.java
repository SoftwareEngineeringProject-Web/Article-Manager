package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private CategoryRepository categoryRepository;

  public List<Object[]> findMonthlyArticlesDataByUserId(Long userId) {
    return articleRepository.findMonthlyArticlesDataByUserId(userId);
  }

  public Integer countByUserId(Long userId) {
    return articleRepository.countByUserId(userId);
  }

  public Integer countByCategoryIdAndUserId(Long categoryId, Long userId) {
    return articleRepository.countByCategoryIdAndUserId(categoryId, userId);
  }

  public Integer getTotalLikesByUserId(Long userId) {
    return articleRepository.getTotalLikesByUserId(userId);
  }

  public Integer getTotalCommentsByUserId(Long userId) {
    return articleRepository.getTotalCommentsByUserId(userId);
  }

  public Integer getTotalViewsByUserId(Long userId) {
    return articleRepository.getTotalViewsByUserId(userId);
  }

  public Integer getTotalFavoritesByUserId(Long userId) {
    return articleRepository.getTotalFavoritesByUserId(userId);
  }

  public void getAllStatistics(User user, Model model){

    Integer totalArticles = countByUserId(user.getId()); // 共发布文章数量
    Integer totalComments = getTotalCommentsByUserId(user.getId()); // 共收到评论数量
    Integer totalLikes = getTotalLikesByUserId(user.getId()); // 共获得点赞数量
    Integer totalViews = getTotalViewsByUserId(user.getId()); // 共浏览文章数量
    Integer totalFavorites = getTotalFavoritesByUserId(user.getId()); // 共收藏文章数量
    List<Category> categories = categoryRepository.findByUserId(user.getId()); // 分类数据
    // 模拟分类文章数量数据
    List<Map<String, Object>> categoryData = new ArrayList<>();
    for (Category category : categories) {
      Integer count = countByCategoryIdAndUserId(category.getId(), user.getId());
      Map<String, Object> data = new HashMap<>();
      data.put("name", category.getName());
      data.put("count", count);
      categoryData.add(data);
    }

    List<Object[]> monthlyArticlesData = findMonthlyArticlesDataByUserId(user.getId());
    List<Map<String, Object>> articlesData = new ArrayList<>();
    for (Object[] articleData : monthlyArticlesData) {
      Map<String, Object> data = new HashMap<>();
      data.put("month", articleData[0]);
      data.put("count", articleData[1]);
      articlesData.add(data);
    }
    model.addAttribute("user", user);
    model.addAttribute("totalArticles", totalArticles);
    model.addAttribute("totalComments", totalComments);
    model.addAttribute("totalLikes", totalLikes);
    model.addAttribute("totalViews", totalViews);
    model.addAttribute("totalFavorites", totalFavorites);
    model.addAttribute("categoryData", categoryData);
    model.addAttribute("monthlyArticlesData", articlesData);
  }
}