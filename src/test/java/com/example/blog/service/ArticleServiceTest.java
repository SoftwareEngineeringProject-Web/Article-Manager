package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

  @InjectMocks
  private ArticleService articleService;

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private FavoriteArticleRepository favoriteArticleRepository;

  @Mock
  private FavoriteRepository favoriteRepository;

  @Mock
  private CommentRepository commentRepository;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testGetArticleById() {
    Article article = new Article();
    article.setId(1L);

    when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

    Article result = articleService.getArticleById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    verify(articleRepository, times(1)).findById(1L);
  }

  @Test
  public void testSaveArticle() {
    Article article = new Article();
    User user = new User();
    user.setUsername("testUser");

    Category category = new Category();
    category.setId(1L);

    when(userRepository.findByUsername("testUser")).thenReturn(user);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    articleService.saveArticle(article, "testUser", 1L, true);

    assertEquals(user, article.getUser());
    assertEquals(category, article.getCategory());
    assertTrue(article.isPublic());
    assertEquals(0, article.getLikes());
    assertEquals(0, article.getViews());
    assertEquals(0, article.getFavorites());
    assertEquals(0, article.getComments());

    verify(articleRepository, times(1)).save(article);
  }

  @Test
  public void testGetArticlesByUserIdPaged() {
    PageRequest pageable = PageRequest.of(0, 10);
    Article article = new Article();
    Page<Article> articlePage = new PageImpl<>(Collections.singletonList(article), pageable, 1);

    when(articleRepository.findPagedByUserId(1L, pageable)).thenReturn(articlePage);

    Page<Article> result = articleService.getArticlesByUserIdPaged(1L, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(articleRepository, times(1)).findPagedByUserId(1L, pageable);
  }

  @Test
  public void testUpdateArticle() {
    Article article = new Article();
    article.setId(1L);

    User user = new User();
    user.setId(1L);

    Category category = new Category();
    category.setId(1L);

    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

    articleService.updateArticle(article, user, 1L, true);

    assertEquals(user, article.getUser());
    assertEquals(category, article.getCategory());
    assertTrue(article.isPublic());

    verify(articleRepository, times(1)).updateArticle(article.getTitle(), article.getContent(), article.getCategory(), article.isPublic(), article.getId());
  }

  @Test
  public void testDeleteById() {
    articleService.deleteById(1L);
    verify(articleRepository, times(1)).deleteById(1L);
  }

  @Test
  public void testGetArticleData() {
    User user = new User();
    user.setId(1L);
    Category category = new Category();
    category.setId(1L);
    category.setName("testCategory");

    Article article = new Article();
    article.setId(1L);
    article.setUser(user);
    article.setCategory(category);
    article.setPublic(true);
    article.setViews(10);
    article.setLikes(5);
    article.setFavorites(2);

    List<Favorite> favoriteList = Arrays.asList(new Favorite(), new Favorite());

    List<Comment> comments = Arrays.asList(new Comment(), new Comment());

    when(favoriteArticleRepository.countByArticleId(1L)).thenReturn(2);
    when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
    when(favoriteRepository.findByUserId(1L)).thenReturn(favoriteList);
    when(commentRepository.findByArticleId(1L)).thenReturn(comments);

    Map<String, Object> result = articleService.getArticleData(1L, article);

    assertNotNull(result);
    assertEquals(article, result.get("article"));
    assertNotNull(result.get("categoryPath"));
    assertEquals(favoriteList, result.get("favoriteList"));
    assertEquals(comments, result.get("comments"));
    assertEquals(2, result.get("favoriteCount"));

    verify(favoriteArticleRepository, times(1)).countByArticleId(1L);
    verify(categoryRepository, times(1)).findById(anyLong());
    verify(favoriteRepository, times(1)).findByUserId(1L);
    verify(commentRepository, times(1)).findByArticleId(1L);
  }
}
