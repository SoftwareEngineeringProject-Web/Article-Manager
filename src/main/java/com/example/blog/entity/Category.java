package com.example.blog.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    public Category() {
    }

    public Category(Long id, String name, Category parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
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

    public String toString() {
        return "Category{id = " + id + ", name = " + name + ", parent = " + parent + "}";
    }

}
