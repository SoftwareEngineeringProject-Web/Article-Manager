package com.example.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.FavoriteArticle;


@Repository
public interface FavoriteArticleRepository extends JpaRepository<FavoriteArticle, Long> {
  @Query("SELECT article FROM FavoriteArticle favoriteArticle JOIN Article article "
       + "WHERE favoriteArticle.articleId = article.id AND favoriteArticle.favoriteId = :favoriteId")
  List<Article> findArticlesByFavoriteId(Long favoriteId);
}
