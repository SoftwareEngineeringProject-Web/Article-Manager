package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


}
