package com.example.blog.entity;

import org.springframework.core.serializer.Serializer;
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
@IdClass(LikeMultiKeys.class)
public class Like {
    @Id
    private Long userId;

    @Id
    private Long articleId;

    public Like() {}
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getarticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

}
