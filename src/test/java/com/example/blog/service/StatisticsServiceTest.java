package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class StatisticsServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStatistics() {
        // Arrange
        User user = new User();
        user.setId(1L);

        Mockito.when(articleRepository.countByUserId(user.getId()))
                .thenReturn(10);
        Mockito.when(articleRepository.getTotalCommentsByUserId(user.getId()))
                .thenReturn(50);
        Mockito.when(articleRepository.getTotalLikesByUserId(user.getId()))
                .thenReturn(100);
        Mockito.when(articleRepository.getTotalViewsByUserId(user.getId()))
                .thenReturn(1000);
        Mockito.when(articleRepository.getTotalFavoritesByUserId(user.getId()))
                .thenReturn(20);

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Category 1", null, user.getId()));
        categories.add(new Category(2L, "Category 2", null, user.getId()));
        Mockito.when(categoryRepository.findByUserId(user.getId()))
                .thenReturn(categories);

        Mockito.when(articleRepository.countByCategoryIdAndUserId(1L, user.getId()))
                .thenReturn(5);
        Mockito.when(articleRepository.countByCategoryIdAndUserId(2L, user.getId()))
                .thenReturn(3);

        List<Object[]> monthlyArticlesData = new ArrayList<>();
        monthlyArticlesData.add(new Object[]{"2023-01", 3});
        monthlyArticlesData.add(new Object[]{"2023-02", 5});
        Mockito.when(articleRepository.findMonthlyArticlesDataByUserId(user.getId()))
                .thenReturn(monthlyArticlesData);

        // Act
        Map<String, Object> statisticsData = statisticsService.getAllStatistics(user);

        // Assert
        Assertions.assertEquals(user, statisticsData.get("user"));
        Assertions.assertEquals(10, statisticsData.get("totalArticles"));
        Assertions.assertEquals(50, statisticsData.get("totalComments"));
        Assertions.assertEquals(100, statisticsData.get("totalLikes"));
        Assertions.assertEquals(1000, statisticsData.get("totalViews"));
        Assertions.assertEquals(20, statisticsData.get("totalFavorites"));

        List<Map<String, Object>> categoryData = (List<Map<String, Object>>) statisticsData.get("categoryData");
        Assertions.assertEquals(2, categoryData.size());
        Assertions.assertEquals("Category 1", categoryData.get(0).get("name"));
        Assertions.assertEquals(5, categoryData.get(0).get("count"));
        Assertions.assertEquals("Category 2", categoryData.get(1).get("name"));
        Assertions.assertEquals(3, categoryData.get(1).get("count"));

        List<Map<String, Object>> articlesData = (List<Map<String, Object>>) statisticsData.get("monthlyArticlesData");
        Assertions.assertEquals(2, articlesData.size());
        Assertions.assertEquals("2023-01", articlesData.get(0).get("month"));
        Assertions.assertEquals(3, articlesData.get(0).get("count"));
        Assertions.assertEquals("2023-02", articlesData.get(1).get("month"));
        Assertions.assertEquals(5, articlesData.get(1).get("count"));
    }
}
