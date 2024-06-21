package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private CommentRepository commentRepository;

    public Map<String, Object> setAllArticlesInformation(String title, Integer page) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Article> articlePages;
    Map<String, Object> articlesData = new HashMap<>();

    if (title != null && !title.isEmpty()) {
      articlePages = articleRepository.findPagedByTitle(title, pageable);
    } else {
      articlePages = articleRepository.findPaged(pageable);
    }

    articlesData.put("articles", articlePages.getContent());
    articlesData.put("currentPage", page);
    articlesData.put("totalPages", articlePages.getTotalPages());
    return articlesData;
  }

  public Map<String, Object> setAllCommentsInformation(Integer page) {
    PageRequest pageable = PageRequest.of(page, 10);
    Page<Comment> commentPages = commentRepository.findPaged(pageable);
    Map<String, Object> commentsData = new HashMap<>();
    commentsData.put("comments", commentPages.getContent());
    commentsData.put("currentPage", page);
    commentsData.put("totalPages", commentPages.getTotalPages());
    return commentsData;
  }

}
