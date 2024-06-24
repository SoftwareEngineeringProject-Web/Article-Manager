package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

  @InjectMocks
  private AdminService adminService;

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private CommentRepository commentRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSetAllArticlesInformationWithoutTitle() {
    int page = 0;
    PageRequest pageable = PageRequest.of(page, 15);

    Article article1 = new Article();
    Article article2 = new Article();

    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);
    when(articleRepository.findPaged(pageable)).thenReturn(articlePage);
    Map<String, Object> result = adminService.setAllArticlesInformation(null, page);

    assertTrue(result.get("articles") instanceof List);
    assertEquals(2, ((List<Article>) result.get("articles")).size());
    assertEquals(page, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));

    verify(articleRepository, times(1)).findPaged(pageable);
  }

  @Test
  public void testSetAllArticlesInformationWithTitle() {
    String title = "Test Title";
    int page = 0;
    PageRequest pageable = PageRequest.of(page, 15);

    Article article1 = new Article();
    article1.setTitle("Test Title");
    Article article2 = new Article();
    article2.setTitle("Another Test Title");

    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);

    when(articleRepository.findPagedByTitle(title, pageable)).thenReturn(articlePage);

    Map<String, Object> result = adminService.setAllArticlesInformation(title, page);

    assertTrue(result.get("articles") instanceof java.util.List);  // Should be a list, not a Page
    assertEquals(2, ((java.util.List<Article>) result.get("articles")).size());
    assertEquals(page, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));

    verify(articleRepository, times(1)).findPagedByTitle(title, pageable);
  }
  @Test
  public void testSetAllCommentsInformation() {
    int page = 0;
    PageRequest pageable = PageRequest.of(page, 15);

    Comment comment1 = new Comment();
    Comment comment2 = new Comment();

    Page<Comment> commentPage = new PageImpl<>(Arrays.asList(comment1, comment2), pageable, 2);
    when(commentRepository.findPaged(pageable)).thenReturn(commentPage);
    Map<String, Object> result = adminService.setAllCommentsInformation(page);

    assertTrue(result.get("comments") instanceof List);
    assertEquals(2, ((List<Comment>) result.get("comments")).size());
    assertEquals(page, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));

    verify(commentRepository, times(1)).findPaged(pageable);
  }
}
