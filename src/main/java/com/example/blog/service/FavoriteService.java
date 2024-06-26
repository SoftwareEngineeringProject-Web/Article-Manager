package com.example.blog.service;

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

  public void update(Favorite favorite) {
    assert (favorite.getId() != null);
    favoriteRepository.save(favorite);
  }

  public void deleteFavoriteByFavoriteId(Long favoriteId){
    List<Long> articleIds = favoriteArticleRepository.findArticleIdsByFavoriteId(favoriteId);
    favoriteRepository.deleteById(favoriteId);
    for (Long articleId : articleIds) {
      articleRepository.updateFavoritesById(articleId, -1);
    }
  }

  public Favorite addFavorite(Long userId, String favoriteName) {
    Favorite favorite = new Favorite(userId, favoriteName);
    favoriteRepository.save(favorite);
    return favorite;
  }
}
