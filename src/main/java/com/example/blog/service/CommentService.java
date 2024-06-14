package com.example.blog.service;

import com.example.blog.entity.Comment;
import com.example.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
  @Autowired
  private CommentRepository commentRepository;

  public Page<Comment> getCommentsByUserIdPaged(Long userId, Pageable pageable) {
    return commentRepository.findPagedByUserId(userId, pageable);
  }

  public List<Comment> getCommentsByArticleId(Long articleId) {
    return commentRepository.findByArticleId(articleId);
  }

  public List<Comment> getCommentsByResponseId(Long responseId) {
    return commentRepository.findByResponseId(responseId);
  }

  public Comment getCommentById(Long id) {
    return commentRepository.findById(id).orElse(null);
  }
  public void deleteCommentById(Long id) {
    commentRepository.deleteById(id);
  }

  public Comment saveComment(Comment comment) {
    return commentRepository.save(comment);
  }
}
