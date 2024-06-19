package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Favorite;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.FavoriteArticleRepository;
import com.example.blog.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

  @Autowired
  private FavoriteRepository favoriteRepository;

  @Autowired
  private FavoriteArticleRepository favoriteArticleRepository;

  @Autowired
  private ArticleRepository articleRepository;

  public Favorite getFavoriteById(Long id) {
    return favoriteRepository.findById(id).orElse(null);
  }

  public List<Favorite> getFavoritesByUserId(Long userId) {
    return favoriteRepository.findByUserId(userId);
  }

  public void deleteById(Long id) {
    favoriteRepository.deleteById(id);
  }

  public void insert(Favorite favorite) {
    favorite.setId(null);
    favoriteRepository.save(favorite);
  }

  public void update(Favorite favorite) {
    assert (favorite.getId() != null);
    favoriteRepository.save(favorite);
  }

  public List<Article> getArticlesByFavoriteId(Long favoriteId) {
    return favoriteArticleRepository.findArticlesByFavoriteId(favoriteId);
  }

  public void deleteFavoriteByFavoriteId(Long favoriteId){
    List<Long> articleIds = favoriteArticleRepository.findArticleIdsByFavoriteId(favoriteId);
    deleteById(favoriteId);
    for (Long articleId : articleIds) {
      articleRepository.updateFavoritesById(articleId, -1);
    }
  }

  public Favorite addFavorite(Long userId, String favoriteName) {
    Favorite favorite = new Favorite(userId, favoriteName);
    insert(favorite);
    return favorite;
  }
}
