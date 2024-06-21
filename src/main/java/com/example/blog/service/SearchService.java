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

  public Map<String, Object> searchArticle(long userId, int pageNo, String keywordInTitle,
                                           String keywordInContent, Instant begin, Instant end) {
    PageRequest pageable = PageRequest.of(pageNo, 10);
    String ContentPattern = keywordInContent == null ? null : "%" + keywordInContent + "%";
    String TitlePattern = keywordInTitle == null ? null : "%" + keywordInTitle + "%";
    Page<Article> articles = null;
    if ( TitlePattern == null && ContentPattern == null) {
      articles = articleRepository.findByCreatedTimeBetween(userId, begin, end, pageable);
    } else if (TitlePattern != null && ContentPattern == null) {
      articles = articleRepository.findByKeywordInTitleAndCreatedTimeBetween(userId, TitlePattern, begin, end, pageable);
    } else if (TitlePattern == null && ContentPattern != null) {
      articles = articleRepository.findByKeywordInContentAndCreatedTimeBetween(userId, ContentPattern, begin, end, pageable);
    } else {
      articles = articleRepository.findByKeywordInTitleAndContentAndCreatedTimeBetween(userId, TitlePattern, ContentPattern, begin, end, pageable);
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("articles", articles.getContent());
    result.put("currentPage", pageNo);
    result.put("totalPages", articles.getTotalPages());
    result.put("keywordInTitle", keywordInTitle);
    result.put("keywordInContent", keywordInContent);
    return result;
  }
}
