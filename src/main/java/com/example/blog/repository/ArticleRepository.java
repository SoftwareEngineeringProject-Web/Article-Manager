package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
  Page<Article> findPagedByUserId(Long userId, Pageable pageable);

  List<Article> findByCategory(Category category);

  void deleteById(Long id);

  Page<Article> findByUserIdAndTitleAndCategory(Long userId, String title, Category category, Pageable pageable);

  Page<Article> findByUserIdAndTitle(Long userId, String title, Pageable pageable);

  Page<Article> findByUserIdAndCategory(Long userId, Category category, Pageable pageable);

  @Query("SELECT FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m') AS month, COUNT(a) AS count " +
      "FROM Article a WHERE a.user.id = ?1 GROUP BY month ORDER BY FUNCTION('DATE_FORMAT', a.createdAt, '%Y-%m')")
  List<Object[]> findMonthlyArticlesDataByUserId(Long userId);

  Integer countByUserId(Long userId);

  Integer countByCategoryIdAndUserId(Long categoryId, Long userId);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.title = ?1, a.content = ?2, a.category = ?3, a.isPublic = ?4 WHERE a.id = ?5")
  void updateArticle(String title, String content, Category category, Boolean isPublic, Long id);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.views = ?2 WHERE a.id = ?1")
  void updateViews(Long id, Integer views);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.likes = ?2 WHERE a.id = ?1")
  void updateLikes(Long id, Integer likes);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.likes = a.likes + (?2) WHERE a.id = ?1")
  void updateLikesById(Long id, Integer moreLikes);

  @Transactional
  @Modifying
  @Query("UPDATE Article a SET a.category = null WHERE a.category.id = :categoryId")
  void setCategoryIdToNullByCategoryId(Long categoryId);

  @Transactional
  @Query("SELECT SUM(a.likes) FROM Article a WHERE a.user.id = ?1")
  Integer getTotalLikesByUserId(Long userId);

  @Transactional
  @Query("SELECT SUM(a.comments) FROM Article a WHERE a.user.id = ?1")
  Integer getTotalCommentsByUserId(Long userId);

  @Transactional
  @Query("SELECT SUM(a.views) FROM Article a WHERE a.user.id = ?1")
  Integer getTotalViewsByUserId(Long userId);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.comments = a.comments + (?2) WHERE a.id = ?1")
  void updateCommentsById(Long id, Integer moreComments);

  @Modifying
  @Transactional
  @Query("UPDATE Article a SET a.favorites = a.favorites + (?2) WHERE a.id = ?1")
  void updateFavoritesById(Long id, Integer moreFavorites);

  @Transactional
  @Query("SELECT SUM(a.favorites) FROM Article a WHERE a.user.id = ?1")
  Integer getTotalFavoritesByUserId(Long userId);

}
