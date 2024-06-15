package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.FavoriteArticle;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FavoriteArticleRepository extends JpaRepository<FavoriteArticle, Long> {
  @Query("SELECT article FROM FavoriteArticle favoriteArticle JOIN Article article "
      + "ON favoriteArticle.articleId = article.id WHERE favoriteArticle.favoriteId = :favoriteId")
  List<Article> findArticlesByFavoriteId(Long favoriteId);

  @Transactional
  void deleteByArticleIdAndFavoriteId(Long articleId, Long favoriteId);
}
