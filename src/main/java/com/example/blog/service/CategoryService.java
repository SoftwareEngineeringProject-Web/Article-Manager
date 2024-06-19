package com.example.blog.service;

import com.example.blog.entity.Category;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ArticleRepository articleRepository;

  public Category getCategoryById(Long id) {
    return id == null ? null : categoryRepository.findById(id).orElse(null);
  }

  public List<Category> findByUserId(Long userId) {
    return categoryRepository.findByUserId(userId);
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

  public void deleteCategory(Long id){
    List<Category> categories = new ArrayList<>();
    Category firstCategory = getCategoryById(id);
    if (firstCategory != null)
      categories.add(firstCategory);

    //为了删除时不破坏Category的外键约束
    //该列表中的元素满足以下约束：如果A是B的父亲，则A一定在B后面
    LinkedList<Category> toBeDeleted = new LinkedList<>();
    while (!categories.isEmpty()) {
      Category last = categories.get(categories.size() - 1);
      categories.remove(categories.size() - 1);
      categories.addAll(findByParentId(last.getId()));
      articleRepository.setCategoryIdToNullByCategoryId(last.getId());
      toBeDeleted.addFirst(last);
    }
    toBeDeleted.forEach(category -> {
      deleteById(category.getId());
    });
  }
}
