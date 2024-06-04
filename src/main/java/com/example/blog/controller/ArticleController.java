package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    public PasswordEncoder passwordEncoder;

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

    @GetMapping("/{username}/home")
    public String userHome(@PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username);
        Long userId = userService.findUserByUsername(username).getId();
        List<Article> articles = articleService.getArticlesByUserId(userId);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("user", user);
        model.addAttribute("articles", articles);
        model.addAttribute("categories", categories);
        return "home";
    }

    @GetMapping("/{username}/change-password")
    public String changePassword(@PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username);
        userService.updateUser(user);
        model.addAttribute("user", user);
        return "change-password";
    }

    @PostMapping("/{username}/change-password")
    public String changePasswordPost(@PathVariable("username") String username,@RequestParam("password") String password) {
        User user = userService.findUserByUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userService.updateUser(user);
        return "redirect:/"+username+"/home";
    }

    @GetMapping("/{username}/change-information")
    public String changeInformation(@PathVariable("username") String username,  Model model) {
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "change-information";
    }

    @PostMapping("/{username}/change-information")
    public String changeInformationPost(@PathVariable("username") String username, @RequestParam("email") String email,
                                        @RequestParam("name") String name, @RequestParam("username") String newUsername) {
        User user = userService.findUserByUsername(username);
        user.setEmail(email);
        user.setUsername(newUsername);
        user.setName(name);
        userService.updateUser(user);
        return "redirect:/"+user.getUsername() +"/home";
    }

    @GetMapping("/{username}/article/{id}")
    public String article(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User user = userService.findUserByUsername(username);
        Article article = articleService.getArticleById(id);
        Category categoryParam = categoryService.getCategoryById(article.getCategory().getId());

        // Construct full category paths
        List<String> categoryPaths = new ArrayList<>();
        Category category =  categoryService.getCategoryById(article.getCategory().getId());
        categoryPaths.add(category.getFullCategoryPath());

        model.addAttribute("user", user);
        model.addAttribute("article", article);
        model.addAttribute("category", categoryParam);
        model.addAttribute("categoryPaths", categoryPaths);
        return "article";
    }

    @GetMapping("/{username}/create-article")
    public String createArticle(@PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        return "create-article";
    }

    @PostMapping("/{username}/create-article")
    public String createArticlePost(@ModelAttribute Article article, @PathVariable("username") String username, @RequestParam("category") Long categoryId) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryById(categoryId);
        article.setCategory(category);
        article.setUser(user);
        articleService.saveArticle(article);
        return "redirect:/" + username + "/home";
    }


}
