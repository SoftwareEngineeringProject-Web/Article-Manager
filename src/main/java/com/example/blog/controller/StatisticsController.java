package com.example.blog.controller;

import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.StatisticsService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {
  @Autowired
  private UserService userService;

  @Autowired
  private ArticleService articleService;

  @Autowired
  private CategoryService categoryService;
  @Autowired
  private StatisticsService statisticsService;

  @ModelAttribute
  public void checkUser(@PathVariable("username") String username) throws ModelAndViewDefiningException {
    // 获取当前登录用户的用户名
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUsername = authentication.getName();

    // 如果当前登录用户与要访问的用户不匹配，则进行相应处理
    if (!loggedInUsername.equals(username)) {
      String redirectUrl = "/" + loggedInUsername + "/home";
      RedirectView redirectView = new RedirectView(redirectUrl);
      throw new ModelAndViewDefiningException(new ModelAndView(redirectView));
    }
  }

  @GetMapping("{username}/statistics")
  public String getStatistics(Model model, @PathVariable String username) {

    User user = userService.findUserByUsername(username);
    int totalArticles = articleService.countByUserId(user.getId()); // 共发布文章数量
    int totalComments = 0; // 共收到评论数量
    int totalLikes = articleService.getTotalLikesByUserId(user.getId()); // 共获得点赞数量
    List<Category> categories = categoryService.findByUserId(user.getId()); // 分类数据
    // 模拟分类文章数量数据
    List<Map<String, Object>> categoryData = new ArrayList<>();
    for (Category category : categories) {
      Integer count = articleService.countByCategoryIdAndUserId(category.getId(), user.getId());
      categoryData.add(createCategoryData(category.getName(), count));
    }

    List<Object[]> monthlyArticlesData = statisticsService.findMonthlyArticlesDataByUserId(user.getId());
    List<Map<String, Object>> articlesData = new ArrayList<>();
    for (Object[] data : monthlyArticlesData) {
      articlesData.add(createMonthlyArticleData((String) data[0], (Long) data[1]));
    }
    model.addAttribute("user", user);
    model.addAttribute("totalArticles", totalArticles);
    model.addAttribute("totalComments", totalComments);
    model.addAttribute("totalLikes", totalLikes);
    model.addAttribute("categoryData", categoryData);
    model.addAttribute("monthlyArticlesData", articlesData);
    return "statistics";
  }

  private Map<String, Object> createCategoryData(String name, int count) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", name);
    data.put("count", count);
    return data;
  }

  private Map<String, Object> createMonthlyArticleData(String month, Long count) {
    Map<String, Object> data = new HashMap<>();
    data.put("month", month);
    data.put("count", count);
    return data;
  }
}
