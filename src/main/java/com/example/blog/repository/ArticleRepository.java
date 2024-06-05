package com.example.blog.repository;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserId(Long userId);
    List<Article> findByCategory(Category category);
}
