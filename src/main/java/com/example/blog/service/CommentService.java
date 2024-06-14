package com.example.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.entity.Comment;
import com.example.blog.repository.CommentRepository;

@Service
public class CommentService {
  @Autowired
  private CommentRepository commentRepository;

  public List<Comment> getCommentsByUserId(Long userId) {
    return commentRepository.findByUserId(userId);
  }

  public List<Comment> getCommentsByArticleId(Long articleId) {
    return commentRepository.findByArticleId(articleId);
  }

  public List<Comment> getCommentsByResponseId(Long responseId) {
    return commentRepository.findByResponseId(responseId);
  }
}
