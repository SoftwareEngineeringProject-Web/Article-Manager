package com.example.blog.controller;


import com.example.blog.data.CategoryTreeNode;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

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

    @GetMapping("/{username}/categories")
    public String showCategoryPage(@PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username);
        List<Category> categories = categoryService.getAllCategories();
        CategoryTreeNode root = null;
        HashMap<Long, CategoryTreeNode> nodes = new HashMap<>();
        for (Category category : categories) {
            CategoryTreeNode currentNode = new CategoryTreeNode(category.getId(), category.getName());
            nodes.put(category.getId(), currentNode);
            if (category.getParent() == null)
                root = currentNode;
        }
        for (Category category : categories) {
            Category parent = category.getParent();
            if (parent != null) {
                nodes.get(parent.getId()).addChild(nodes.get(category.getId()));
            }
        }

        model.addAttribute("rootCategory", root);
        model.addAttribute("user", user);
        return "categories";
    }
}
