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
    void deleteById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.title = ?1, a.content = ?2, a.category = ?3 WHERE a.id = ?4")
    void updateArticle(String title, String content, Category category, Long id);
}
