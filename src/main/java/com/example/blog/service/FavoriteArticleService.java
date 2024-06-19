package com.example.blog.service;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.FavoriteArticle;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.FavoriteArticleRepository;
import com.example.blog.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteArticleService {

  @Autowired
  private FavoriteArticleRepository favoriteArticleRepository;
  @Autowired
  private FavoriteRepository favoriteRepository;
  @Autowired
  private ArticleRepository articleRepository;

  public void favoriteArticle(Long favoriteId, Long articleId) {
    favoriteArticleRepository.save(new FavoriteArticle(favoriteId, articleId));
  }

  public void deleteFavoriteArticle(Long articleId, Long favoriteId) {
    favoriteArticleRepository.deleteByArticleIdAndFavoriteId(articleId, favoriteId);
    articleRepository.updateFavoritesById(articleId, -1);
  }

  public int countFavoriteArticle(Long articleId) {
    return favoriteArticleRepository.countByArticleId(articleId);
  }

  public List<Long> findArticleIdsByFavoriteId(Long favoriteId) {
    return favoriteArticleRepository.findArticleIdsByFavoriteId(favoriteId);
  }

  public FavoriteArticle findByArticleIdAndFavoriteId(Long articleId, Long favoriteId) {
    return favoriteArticleRepository.findByArticleIdAndFavoriteId(articleId, favoriteId);
  }
  public ArrayList<FavoriteWithArticles> getFavoriteArticleListByUserId(Long userId){
    List<Favorite> favorites = favoriteRepository.findByUserId(userId);
    ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
    favorites.forEach((favorite) -> {
      favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
          favoriteArticleRepository.findArticlesByFavoriteId(favorite.getId())));
    });
    return favoriteWithArticlesList;
  }

  public Integer addFavoriteArticle(Long favoriteId, Long articleId){
    FavoriteArticle favoriteArticle = findByArticleIdAndFavoriteId(articleId, favoriteId);
    if (favoriteArticle != null) {
      return null;
    } else {
      favoriteArticle(favoriteId, articleId);
      articleRepository.updateFavoritesById(articleId, 1);
      Article article = articleRepository.findById(articleId).orElse(null);
      if(article == null){
        return null;
      } else {
        return article.getFavorites();
      }
    }
  }
}
