package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.service.ArticleService;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ArticleController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{username}/home")
    public String userHome(@PathVariable("username") String username, Model model) {
        Long userId = userService.findUserByUsername(username).getId();
        List<Article> articles = articleService.getArticlesByUserId(userId);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        return "home";
    }
}
