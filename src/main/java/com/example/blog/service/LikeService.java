package com.example.blog.service;

import com.example.blog.entity.Like;
import com.example.blog.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    public Like getByUserIdAndArticleId(Long userId, Long articleId) {
        return likeRepository.findByUserIdAndArticleId(userId, articleId);
    }
    public void insertByUserIdAndArticleId(Long userId, Long articleId) {
        likeRepository.insertByUserIdAndArticleId(userId, articleId);
    }
    public void deleteByUserIdAndArticleId(Long userId, Long articleId) {
        likeRepository.deleteByUserIdAndArticleId(userId, articleId);
    }


}
