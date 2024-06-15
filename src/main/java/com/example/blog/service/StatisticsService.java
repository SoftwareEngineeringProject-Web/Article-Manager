package com.example.blog.service;

import com.example.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

  @Autowired
  private ArticleRepository articleRepository;

  public List<Object[]> findMonthlyArticlesDataByUserId(Long userId) {
    return articleRepository.findMonthlyArticlesDataByUserId(userId);
  }
}
