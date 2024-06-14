package com.example.blog.repository;

import com.example.blog.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleId(Long parentId);
    List<Comment> findByUserId(Long userId);
    List<Comment> findByResponseId(Long responseId);

    void deleteById(Long id);
}
