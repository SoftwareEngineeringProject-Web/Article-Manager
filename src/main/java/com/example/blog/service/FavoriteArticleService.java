package com.example.blog.service;

import com.example.blog.entity.FavoriteArticle;
import com.example.blog.repository.FavoriteArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteArticleService {

  @Autowired
  private FavoriteArticleRepository favoriteArticleRepository;

  public void favoriteArticle(Long favoriteId, Long articleId) {
    favoriteArticleRepository.save(new FavoriteArticle(favoriteId, articleId));
  }

  public void deleteFavoriteArticle(Long articleId, Long favoriteId) {
    favoriteArticleRepository.deleteByArticleIdAndFavoriteId(articleId, favoriteId);
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
}
