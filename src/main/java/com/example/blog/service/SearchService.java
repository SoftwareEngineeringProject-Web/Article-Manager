package com.example.blog.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.blog.entity.Article;
import com.example.blog.repository.ArticleRepository;


@Service
public class SearchService {
  @Autowired
  private ArticleRepository articleRepository;

  public Map<String, Object> searchArticle(int pageNo, String keyword, Instant begin, Instant end) {
    PageRequest pageable = PageRequest.of(pageNo, 10);
    Page<Article> articles = keyword == null ? articleRepository.findByCreatedTimeBetween(begin, end, pageable) :
                             begin == null   ? articleRepository.findByKeywordInTitle(keyword, pageable)        :
                             articleRepository.findByKeywordInTitleAndCreatedTimeBetween(keyword, begin, end, pageable);
    Map<String, Object> result = new HashMap<String,Object>();
    result.put("articles", articles.getContent());
    result.put("currentPage", pageNo);
    result.put("totalPages", articles.getTotalPages());
    return result;
  }
}
