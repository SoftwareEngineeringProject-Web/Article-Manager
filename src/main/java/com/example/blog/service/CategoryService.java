package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return id == null ? null : categoryRepository.findById(id).orElse(null);
    }

    public List<Category> findCategoriesByUserId(Long userId) {
        return categoryRepository.findCategoriesByUserId(userId);
    }

    public List<Category> findByParentId(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long id) {
      categoryRepository.deleteById(id);
    }
}
