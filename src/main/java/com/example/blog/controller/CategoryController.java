package com.example.blog.controller;


import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class CategoryController {
  @Autowired
  private UserService userService;
  @Autowired
  private CategoryService categoryService;


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
    categoryService.deleteCategory(id);
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

    Map<String, Object> categoryTree = categoryService.getCategoryTree(username, categoryId, page);
    model.addAllAttributes(categoryTree);
    return "categories";
  }
}
