package com.example.blog.service;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.relation.FavoriteArticle;
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
    FavoriteArticle favoriteArticle = favoriteArticleRepository.findByArticleIdAndFavoriteId(articleId, favoriteId);
    if (favoriteArticle != null) {
      return null;
    } else {
      Article article = articleRepository.findById(articleId).orElse(null);
      if(article == null) {
        return null;
      }
      favoriteArticle(favoriteId, articleId);
      articleRepository.updateFavoritesById(articleId, 1);
      return article.getFavorites();
    }
  }
}
