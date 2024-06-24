package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.LikeRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LikeService {
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ArticleRepository articleRepository;

  public Integer getLikeCountByArticleId(Long articleId, String username) {
    Article article = articleRepository.findById(articleId).orElse(null);
    if (article == null) {
      return null;
    }
    Long userId = userRepository.findByUsername(username).getId();
    if (likeRepository.findByUserIdAndArticleId(userId, articleId) == null) {
      articleRepository.updateLikesById(articleId, 1);
      likeRepository.insertByUserIdAndArticleId(userId, articleId);
      return article.getLikes() + 1;
    } else {
      articleRepository.updateLikesById(articleId, -1);
      likeRepository.deleteByUserIdAndArticleId(userId, articleId);
      return article.getLikes() - 1;
    }
  }
}
