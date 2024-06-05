package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.entity.Category;
import com.example.blog.service.CategoryService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Iterator;
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
    public String userHome(@PathVariable("username") String username,@RequestParam(name = "page", defaultValue = "0") int page,
                           Model model) {
        User user = userService.findUserByUsername(username);
        Long userId = userService.findUserByUsername(username).getId();
//        List<Article> articles = articleService.getArticlesByUserId(userId);
        List<Category> categories = categoryService.getAllCategories();

        // 分页处理
        Pageable pageable = PageRequest.of(page, 10); // 每页显示10篇文章
        Page<Article> articlePage = articleService.getArticlesByUserIdPaged(userId, pageable);

        for(Category category:categories){
            List<Article> categoryArticles = articleService.getArticlesByCategory(category);
            // 使用 Iterator 进行迭代并移除不属于此登录用户的文章
            Iterator<Article> iterator = categoryArticles.iterator();
            while (iterator.hasNext()) {
                Article article = iterator.next();
                if (!article.getUser().getId().equals(userId)) {
                    iterator.remove();
                }
            }
            category.setArticles(categoryArticles);
        }
        model.addAttribute("user", user);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
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

    @GetMapping("/{username}/manage")
    public String articleManagePage(@PathVariable("username") String username,
                                    @RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        User user = userService.findUserByUsername(username);
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(page, 20); // 每页显示20篇标题
        Page<Article> articlePages = articleService.getArticlesByUserIdPaged(userId, pageable);

        model.addAttribute("user", user);
        model.addAttribute("articles", articlePages.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePages.getTotalPages());
        return "manage";
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
        article.setCreatedAt();
        articleService.saveArticle(article);
        return "redirect:/" + username + "/home";
    }


    @GetMapping("/{username}/edit-article/{id}")
    public String editArticleForm(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        Article article = articleService.getArticleById(id);
        User user = userService.findUserByUsername(username);
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("article", article);
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        return "edit-article";
    }
    @PostMapping("/{username}/edit-article/{id}")
    public String editArticle(@ModelAttribute Article article, @PathVariable("username") String username, @PathVariable("id") Long id,
                              @RequestParam("category") Long categoryId) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryById(categoryId);
        article.setCategory(category);
        article.setUser(user);
        articleService.updateArticle(article);
        return "redirect:/" + username + "/manage";
    }

    @GetMapping("/{username}/delete-article/{id}")
    public String deleteArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId) {
        articleService.deleteById(articleId);
        return "forward:/" + username + "/manage";
    }
}
