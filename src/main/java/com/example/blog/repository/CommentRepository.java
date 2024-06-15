package com.example.blog.repository;

import com.example.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  List<Comment> findByArticleId(Long parentId);

  List<Comment> findByUserId(Long userId);

  Page<Comment> findPagedByUserId(Long userId, Pageable pageable);

  @Query("SELECT comment FROM Comment comment WHERE comment.responseTo.id = :responseId")
  List<Comment> findByResponseId(Long responseId);

  void deleteById(Long id);
}
