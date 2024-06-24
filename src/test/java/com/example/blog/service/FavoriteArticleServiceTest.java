package com.example.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.relation.FavoriteArticle;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.FavoriteArticleRepository;
import com.example.blog.repository.FavoriteRepository;

class FavoriteArticleServiceTest {

  @InjectMocks
  private FavoriteArticleService favoriteArticleService;

  @Mock
  private FavoriteArticleRepository favoriteArticleRepository;

  @Mock
  private FavoriteRepository favoriteRepository;

  @Mock
  private ArticleRepository articleRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFavoriteArticle() {
    Long favoriteId = 1L;
    Long articleId = 2L;

    favoriteArticleService.favoriteArticle(favoriteId, articleId);

    ArgumentCaptor<FavoriteArticle> captor = ArgumentCaptor.forClass(FavoriteArticle.class);
    Mockito.verify(favoriteArticleRepository, Mockito.times(1))
      .save(captor.capture());
    FavoriteArticle savedFavoriteArticle = captor.getValue();
    assertEquals(favoriteId, savedFavoriteArticle.getFavoriteId());
    assertEquals(articleId, savedFavoriteArticle.getArticleId());
  }

  @Test
  void testDeleteFavoriteArticle() {
    Long articleId = 1L;
    Long favoriteId = 2L;

    favoriteArticleService.deleteFavoriteArticle(articleId, favoriteId);

    Mockito.verify(favoriteArticleRepository, Mockito.times(1))
      .deleteByArticleIdAndFavoriteId(articleId, favoriteId);
    Mockito.verify(articleRepository, Mockito.times(1))
      .updateFavoritesById(articleId, -1);
  }

  @Test
  void testGetFavoriteArticleListByUserId() {
    Long userId = 1L;
    List<Favorite> favorites = new ArrayList<>();
    Favorite favorite1 = new Favorite(userId, "Favorite 1");
    Favorite favorite2 = new Favorite(userId, "Favorite 2");
    favorite1.setId(1L);
    favorite2.setId(2L);
    favorites.add(favorite1);
    favorites.add(favorite2);

    Mockito.when(favoriteRepository.findByUserId(userId))
      .thenReturn(favorites);

    List<Article> articles1 = new ArrayList<>();

    Article article1 = new Article(1L, "Article 1", "Content 1", null, null);
    Article article2 = new Article(2L, "Article 2", "Content 2", null, null);
    articles1.add(article1);
    articles1.add(article2);

    List<Article> articles2 = new ArrayList<>();
    Article article3 = new Article(1L, "Article 3", "Content 3", null, null);
    Article article4 = new Article(1L, "Article 4", "Content 4", null, null);
    articles2.add(article3);
    articles2.add(article4);

    Mockito.when(favoriteArticleRepository.findArticlesByFavoriteId(favorite1.getId()))
      .thenReturn(articles1);
    Mockito.when(favoriteArticleRepository.findArticlesByFavoriteId(favorite2.getId()))
      .thenReturn(articles2);

    ArrayList<FavoriteWithArticles> result = favoriteArticleService.getFavoriteArticleListByUserId(userId);

    Assertions.assertEquals(2, result.size());
    Assertions.assertEquals(favorite1, result.get(0).getFavorite());
    Assertions.assertEquals(articles1, result.get(0).getArticles());
    Assertions.assertEquals(favorite2, result.get(1).getFavorite());
    Assertions.assertEquals(articles2, result.get(1).getArticles());
  }

  @Test
  void testAddFavoriteArticle_Success() {
    Long favoriteId = 1L;
    Long articleId = 2L;

    Mockito.when(favoriteArticleRepository.findByArticleIdAndFavoriteId(articleId, favoriteId))
      .thenReturn(null);

    Article article = new Article(articleId, "Article 1", "Content", null, null);
    article.setFavorites(5);
    Mockito.when(articleRepository.findById(articleId))
      .thenReturn(Optional.of(article));
    Mockito.doAnswer(invocation -> {
      Integer moreFavorites = invocation.getArgument(1);
      article.setFavorites(article.getFavorites() + moreFavorites);
      return 1;
    }).when(articleRepository).updateFavoritesById(Mockito.eq(articleId), Mockito.anyInt());

    Integer result = favoriteArticleService.addFavoriteArticle(favoriteId, articleId);

    ArgumentCaptor<FavoriteArticle> captor = ArgumentCaptor.forClass(FavoriteArticle.class);
    Mockito.verify(favoriteArticleRepository, Mockito.times(1))
      .save(captor.capture());
    FavoriteArticle savedFavoriteArticle = captor.getValue();
    assertEquals(favoriteId, savedFavoriteArticle.getFavoriteId());
    assertEquals(articleId, savedFavoriteArticle.getArticleId());
    Mockito.verify(articleRepository, Mockito.times(1))
      .updateFavoritesById(articleId, 1);
    Assertions.assertEquals(6, result);
  }

  @Test
  void testAddFavoriteArticle_Duplicate() {
    Long favoriteId = 1L;
    Long articleId = 2L;

    FavoriteArticle existingFavoriteArticle = new FavoriteArticle(favoriteId, articleId);
    Mockito.when(favoriteArticleRepository.findByArticleIdAndFavoriteId(articleId, favoriteId))
      .thenReturn(existingFavoriteArticle);

    Integer result = favoriteArticleService.addFavoriteArticle(favoriteId, articleId);

    Mockito.verify(favoriteArticleRepository, Mockito.times(0))
      .save(Mockito.any(FavoriteArticle.class));
    Mockito.verify(articleRepository, Mockito.times(0))
      .updateFavoritesById(Mockito.anyLong(), Mockito.anyInt());
    Assertions.assertNull(result);
  }

  @Test
  void testAddFavoriteArticle_ArticleNotFound() {
    Long favoriteId = 1L;
    Long articleId = 2L;

    Mockito.when(favoriteArticleRepository.findByArticleIdAndFavoriteId(articleId, favoriteId))
      .thenReturn(null);

    Mockito.when(articleRepository.findById(articleId))
      .thenReturn(Optional.empty());

    Integer result = favoriteArticleService.addFavoriteArticle(favoriteId, articleId);
    Assertions.assertNull(result);
  }
}
