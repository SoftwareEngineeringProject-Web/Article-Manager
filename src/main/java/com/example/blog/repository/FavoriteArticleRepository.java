package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.relation.FavoriteArticle;
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

  @Query("SELECT COUNT(favoriteArticle) FROM FavoriteArticle favoriteArticle WHERE favoriteArticle.articleId = :articleId")
  int countByArticleId(Long articleId);

  @Transactional
  @Query("SELECT a.id FROM Article a , FavoriteArticle f WHERE a.id = f.articleId AND f.favoriteId = ?1")
  List<Long> findArticleIdsByFavoriteId(Long favoriteId);

  FavoriteArticle findByArticleIdAndFavoriteId(Long articleId, Long favoriteId);
}
