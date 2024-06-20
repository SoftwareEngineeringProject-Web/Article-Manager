package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Service
public class SearchService {
  @Autowired
  private ArticleRepository articleRepository;

  public Map<String, Object> searchArticle(long userId, int pageNo, String keyword, Instant begin, Instant end) {
    PageRequest pageable = PageRequest.of(pageNo, 10);
    Page<Article> articles = keyword.isEmpty() ? articleRepository.findByCreatedTimeBetween(userId, begin, end, pageable) :
                             articleRepository.findByKeywordInTitleAndCreatedTimeBetween(userId,"%" + keyword + "%", begin, end, pageable);
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("articles", articles.getContent());
    result.put("currentPage", pageNo);
    result.put("totalPages", articles.getTotalPages());
    result.put("keyword", keyword);
    return result;
  }
}
