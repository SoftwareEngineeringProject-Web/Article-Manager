package com.example.blog.service;

import com.example.blog.data.CategoryTreeNode;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ArticleRepository articleRepository;
  @Autowired
  private UserRepository userRepository;

  public Category getCategoryById(Long id) {
    return id == null ? null : categoryRepository.findById(id).orElse(null);
  }

  public List<Category> findByUserId(Long userId) {
    return categoryRepository.findByUserId(userId);
  }

  public Category saveCategory(Category category) {
    return categoryRepository.save(category);
  }

  public void deleteAllCategoryByUserId(Long userId){
    findByUserId(userId).forEach(category -> {
      if (category.getParent() == null)
        deleteCategory(category.getId());
    });
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
      categories.addAll(categoryRepository.findByParentId(last.getId()));
      articleRepository.setCategoryIdToNullByCategoryId(last.getId());
      toBeDeleted.addFirst(last);
    }
    toBeDeleted.forEach(category -> {
      categoryRepository.deleteById(category.getId());
    });
  }

  public Map<String, Object> getCategoryTree(String username, Long categoryId, int page) {
    Map<String, Object> result = new HashMap<>();
    User user = userRepository.findByUsername(username);
    List<Category> categories = findByUserId(user.getId());
    HashMap<Long, CategoryTreeNode> nodes = new HashMap<>();
    Pageable pageable = PageRequest.of(page, 10); // 每页显示10篇文章
    Page<Article> articles = null;
    articles = articleRepository.findByUserIdAndCategory(user.getId(), getCategoryById(categoryId), pageable);

    CategoryTreeNode dummyRoot = new CategoryTreeNode(-1L, null);
    for (Category category : categories) {
      CategoryTreeNode currentNode = new CategoryTreeNode(category.getId(), category.getName());
      nodes.put(category.getId(), currentNode);
    }
    for (Category category : categories) {
      Category parent = category.getParent();
      CategoryTreeNode parentNode = parent == null ? dummyRoot : nodes.get(parent.getId());
      parentNode.addChild(nodes.get(category.getId()));
    }

    result.put("articles", articles.getContent());
    result.put("rootCategory", dummyRoot);
    result.put("user", user);
    result.put("currentCategoryId", categoryId);
    result.put("currentCategoryName", categoryId != null ? nodes.get(categoryId).getName() : null);
    result.put("currentPage", page);
    result.put("totalPages", articles.getTotalPages());
    return result;
  }
}
