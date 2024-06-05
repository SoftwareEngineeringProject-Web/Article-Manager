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
    Page<Article> findByUserId(Long userId, Pageable pageable);
    List<Article> findByCategory(Category category);

    @Modifying
    @Transactional
    @Query("update Article a set a.title =?2, a.content = ?3, a.category = ?4 where a.id = ?1")
    void updateArticle(Article article);
}
