package com.example.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.entity.FavoriteArticle;
import com.example.blog.repository.FavoriteArticleRepository;

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
}
