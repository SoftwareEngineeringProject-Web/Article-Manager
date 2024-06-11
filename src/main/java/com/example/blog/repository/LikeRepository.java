package com.example.blog.repository;

import com.example.blog.entity.Like;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    @Query("SELECT like FROM Like like WHERE like.userId = ?1 AND like.articleId = ?2")
    Like findByUserIdAndArticleId(Long userId, Long articleId);
}
