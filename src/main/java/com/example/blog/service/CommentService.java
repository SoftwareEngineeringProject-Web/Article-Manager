package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
  @Autowired
  private CommentRepository commentRepository;
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private UserRepository userRepository;

  public Comment getCommentById(Long id) {
    return commentRepository.findById(id).orElse(null);
  }
  public void deleteComment(Comment comment) {
    articleRepository.updateCommentsById(comment.getArticle().getId(), -1);
    commentRepository.deleteById(comment.getId());
  }

  public void submitComment(String username , Long articleId, String comment){
    User user = userRepository.findByUsername(username);
    articleRepository.updateCommentsById(articleId, 1);
    Article article = articleRepository.findById(articleId).orElse(null);
    Comment commentEntity = new Comment(user, article, comment, null);
    commentRepository.save(commentEntity);
  }
}
