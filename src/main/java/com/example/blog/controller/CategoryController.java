package com.example.blog.controller;


import com.example.blog.data.CategoryTreeNode;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Controller
public class CategoryController {
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;

  @Autowired
  private ArticleService articleService;

  @ModelAttribute
  public void checkUser(@PathVariable("username") String username) throws ModelAndViewDefiningException {
    // 获取当前登录用户的用户名
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUsername = authentication.getName();

    // 如果当前登录用户与要访问的用户不匹配，则进行相应处理
    if (!loggedInUsername.equals(username)) {
      String redirectUrl = "/" + loggedInUsername + "/home";
      RedirectView redirectView = new RedirectView(redirectUrl);
      throw new ModelAndViewDefiningException(new ModelAndView(redirectView));
    }
  }

  @GetMapping("/{username}/delete-category/{id}")
  public String deleteCategory(@PathVariable("username") String username, @PathVariable("id") Long id) {
    List<Category> categories = new ArrayList<>();
    Category firstCategory = categoryService.getCategoryById(id);
    if (firstCategory != null)
      categories.add(firstCategory);

    //为了删除时不破坏Category的外键约束
    //该列表中的元素满足以下约束：如果A是B的父亲，则A一定在B后面
    LinkedList<Category> toBeDeleted = new LinkedList<>();
    while (!categories.isEmpty()) {
      Category last = categories.get(categories.size() - 1);
      categories.remove(categories.size() - 1);
      categories.addAll(categoryService.findByParentId(last.getId()));
      articleService.setCategoryIdtoNullByCategoryId(last.getId());
      toBeDeleted.addFirst(last);
    }
    toBeDeleted.forEach(category -> {
      categoryService.deleteById(category.getId());
    });
    return "redirect:/" + username + "/categories";
  }

  @PostMapping("/{username}/create-category")
  public String createCategory(@PathVariable("username") String username,
                               @RequestParam(name = "parentId", required = false) Long parentId,
                               @RequestParam(name = "categoryName", required = true) String categoryName) {
    Category category = new Category(null, categoryName, categoryService.getCategoryById(parentId), userService.findUserByUsername(username).getId());
    categoryService.saveCategory(category);
    return "redirect:/" + username + "/categories" + (parentId != null ? "?categoryId=" + parentId : "");
  }

  @GetMapping("/{username}/categories")
  public String showCategoryPage(@PathVariable("username") String username,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "categoryId", required = false) Long categoryId, Model model) {
    User user = userService.findUserByUsername(username);
    List<Category> categories = categoryService.findByUserId(user.getId());
    HashMap<Long, CategoryTreeNode> nodes = new HashMap<>();
    Pageable pageable = PageRequest.of(page, 10); // 每页显示10篇文章
    Page<Article> articles = null;
    CategoryTreeNode dummyRoot = new CategoryTreeNode(-1L, null);
    if (categoryId != null) {
      Category categoryParam = categoryService.getCategoryById(categoryId);
      articles = articleService.getArticlesByCategoryPaged(user.getId(), categoryParam, pageable);
    } else {
      articles = new PageImpl<>(new ArrayList<>(), pageable, 0);
    }
    for (Category category : categories) {
      CategoryTreeNode currentNode = new CategoryTreeNode(category.getId(), category.getName());
      nodes.put(category.getId(), currentNode);
    }
    for (Category category : categories) {
      Category parent = category.getParent();
      CategoryTreeNode parentNode = parent == null ? dummyRoot : nodes.get(parent.getId());
      parentNode.addChild(nodes.get(category.getId()));
    }

    model.addAttribute("articles", articles.getContent());
    model.addAttribute("rootCategory", dummyRoot);
    model.addAttribute("user", user);
    model.addAttribute("currentCategoryId", categoryId);
    model.addAttribute("currentCategoryName", categoryId != null ? nodes.get(categoryId).getName() : null);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", articles.getTotalPages());
    return "categories";
  }
}
