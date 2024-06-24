package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private SearchService searchService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testSearchArticleByCreatedTime() {
    long userId = 1L;
    int pageNo = 0;
    String titlePattern = null;
    String contentPattern = null;
    Instant begin = Instant.parse("2023-01-01T00:00:00Z");
    Instant end = Instant.parse("2023-12-31T23:59:59Z");

    PageRequest pageable = PageRequest.of(pageNo, 10);
    Article article1 = new Article();
    Article article2 = new Article();
    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);

    when(articleRepository.findByCreatedTimeBetween(userId, begin, end, pageable)).thenReturn(articlePage);

    Map<String, Object> result = searchService.searchArticle(userId, pageNo, titlePattern, contentPattern, begin, end);

    assertEquals(articlePage.getContent(), result.get("articles"));
    assertEquals(pageNo, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));
  }

  @Test
  public void testSearchArticleByTitle() {
    long userId = 1L;
    int pageNo = 0;
    String titlePattern = "test";
    String contentPattern = null;
    Instant begin = Instant.parse("2023-01-01T00:00:00Z");
    Instant end = Instant.parse("2023-12-31T23:59:59Z");

    PageRequest pageable = PageRequest.of(pageNo, 10);
    Article article1 = new Article();
    Article article2 = new Article();
    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);

    when(articleRepository.findByKeywordInTitleAndCreatedTimeBetween(userId, titlePattern, begin, end, pageable)).thenReturn(articlePage);

    Map<String, Object> result = searchService.searchArticle(userId, pageNo, titlePattern, contentPattern, begin, end);

    assertEquals(articlePage.getContent(), result.get("articles"));
    assertEquals(pageNo, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));
  }

  @Test
  public void testSearchArticleByContent() {
    long userId = 1L;
    int pageNo = 0;
    String titlePattern = null;
    String contentPattern = "test";
    Instant begin = Instant.parse("2023-01-01T00:00:00Z");
    Instant end = Instant.parse("2023-12-31T23:59:59Z");

    PageRequest pageable = PageRequest.of(pageNo, 10);
    Article article1 = new Article();
    Article article2 = new Article();
    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);

    when(articleRepository.findByKeywordInContentAndCreatedTimeBetween(userId, contentPattern, begin, end, pageable)).thenReturn(articlePage);

    Map<String, Object> result = searchService.searchArticle(userId, pageNo, titlePattern, contentPattern, begin, end);

    assertEquals(articlePage.getContent(), result.get("articles"));
    assertEquals(pageNo, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));
  }

  @Test
  public void testSearchArticleByTitleAndContent() {
    long userId = 1L;
    int pageNo = 0;
    String titlePattern = "title";
    String contentPattern = "content";
    Instant begin = Instant.parse("2023-01-01T00:00:00Z");
    Instant end = Instant.parse("2023-12-31T23:59:59Z");

    PageRequest pageable = PageRequest.of(pageNo, 10);
    Article article1 = new Article();
    Article article2 = new Article();
    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);

    when(articleRepository.findByKeywordInTitleAndContentAndCreatedTimeBetween(userId, titlePattern, contentPattern, begin, end, pageable)).thenReturn(articlePage);

    Map<String, Object> result = searchService.searchArticle(userId, pageNo, titlePattern, contentPattern, begin, end);

    assertEquals(articlePage.getContent(), result.get("articles"));
    assertEquals(pageNo, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));
  }
}
