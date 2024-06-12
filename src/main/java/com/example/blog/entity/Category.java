package com.example.blog.entity;

import jakarta.persistence.*;
import com.example.blog.entity.Article;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "category")
    private List<Article> articles;

    public Category() {
    }

    public Category(Long id, String name, Category parent, Long userId) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.userId = userId;
    }

    public Category(Long id, String name, Category parent, List<Article> articles) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.articles = articles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Category getParent() {
        return parent;
    }


    public void setParent(Category parent) {
        this.parent = parent;
    }

    public String getFullCategoryPath() {
        StringBuilder path = new StringBuilder(this.name);
        Category current = this.parent;
        while (current != null) {
            path.insert(0, current.name + " > ");
            current = current.parent;
        }
        return path.toString();
    }
    public String toString() {
        return "Category{id = " + id + ", name = " + name + ", parent = " + parent + "}";
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
