package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

  public Map<String, Object> getAllStatistics(User user){
    Map<String, Object> statisticsData = new HashMap<>();
    Integer totalArticles = articleRepository.countByUserId(user.getId()); // 共发布文章数量
    Integer totalComments = articleRepository.getTotalCommentsByUserId(user.getId()); // 共收到评论数量
    Integer totalLikes = articleRepository.getTotalLikesByUserId(user.getId()); // 共获得点赞数量
    Integer totalViews = articleRepository.getTotalViewsByUserId(user.getId()); // 共浏览文章数量
    Integer totalFavorites = articleRepository.getTotalFavoritesByUserId(user.getId()); // 共收藏文章数量
    List<Category> categories = categoryRepository.findByUserId(user.getId()); // 分类数据
    // 模拟分类文章数量数据
    List<Map<String, Object>> categoryData = new ArrayList<>();
    for (Category category : categories) {
      Integer count = articleRepository.countByCategoryIdAndUserId(category.getId(), user.getId());
      Map<String, Object> data = new HashMap<>();
      data.put("name", category.getName());
      data.put("count", count);
      categoryData.add(data);
    }

    List<Object[]> monthlyArticlesData = articleRepository.findMonthlyArticlesDataByUserId(user.getId());
    List<Map<String, Object>> articlesData = new ArrayList<>();
    for (Object[] articleData : monthlyArticlesData) {
      Map<String, Object> data = new HashMap<>();
      data.put("month", articleData[0]);
      data.put("count", articleData[1]);
      articlesData.add(data);
    }
    statisticsData.put("user", user);
    statisticsData.put("totalArticles", totalArticles);
    statisticsData.put("totalComments", totalComments);
    statisticsData.put("totalLikes", totalLikes);
    statisticsData.put("totalViews", totalViews);
    statisticsData.put("totalFavorites", totalFavorites);
    statisticsData.put("categoryData", categoryData);
    statisticsData.put("monthlyArticlesData", articlesData);
    return statisticsData;
  }
}