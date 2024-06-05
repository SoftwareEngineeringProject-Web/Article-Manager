package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

//    public List<Article> getArticlesByUserId(Long userId) {
//        return articleRepository.findByUserId(userId);
//    }
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId).orElse(null);
    }
    public void saveArticle(Article article) {
        articleRepository.save(article);
    }
    public List<Article> getArticlesByCategory(Category category){
        return articleRepository.findByCategory(category);
    }
    public Page<Article> getArticlesByUserIdPaged(Long userId, Pageable pageable) {
        return articleRepository.findByUserId(userId, pageable);
    }
    public void updateArticle(Article article){
        articleRepository.updateArticle(article.getTitle(), article.getContent(), article.getCategory(), article.getId());
    }
}
