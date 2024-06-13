package com.example.blog.controller;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.User;
import com.example.blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ArticleController {
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FavoriteArticleService favoriteArticleService;
    @Autowired
    private FavoriteService favoriteService;
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

        List<Favorite> favorites = favoriteService.getFavoritesByUserId(userId);
        ArrayList<FavoriteWithArticles> favoriteWithArticlesList = new ArrayList<>();
        favorites.forEach((favorite) -> {
            favoriteWithArticlesList.add(new FavoriteWithArticles(favorite,
                favoriteService.getArticlesByFavoriteId(favorite.getId())));
        });

        // 分页处理
        Pageable pageable = PageRequest.of(page, 10); // 每页显示10篇文章
        Page<Article> articlePage = articleService.getArticlesByUserIdPaged(userId, pageable);

        model.addAttribute("user", user);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("favoriteWithArticlesList", favoriteWithArticlesList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        return "home";
    }

    @GetMapping("/{username}/article/{id}")
    public String article(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User user = userService.findUserByUsername(username);
        Article article = articleService.getArticleById(id);

        // 检查用户要访问的文章是否为自己的文章，或者是否为共享
        if (article.getUser().getId() != user.getId() && !article.isPublic()) {
          return "redirect:/" + username + "/access-denied";
        }
        // Construct full category paths
        Category category = categoryService.getCategoryById(article.getCategory() == null ? null : article.getCategory().getId());
        String categoryPath = category == null ? null : category.getFullCategoryPath();
        List<Favorite> favoriteList = favoriteService.getFavoritesByUserId(user.getId());
        article.incrementViews();
        articleService.updateViews(article);

        model.addAttribute("user", user);
        model.addAttribute("article", article);
        model.addAttribute("categoryPath", categoryPath);
        model.addAttribute("favoriteList", favoriteList);
        return "article";
    }

    @GetMapping("/{username}/create-article")
    public String createArticle(@PathVariable("username") String username, Model model) {
        User user = userService.findUserByUsername(username);
        List<Category> categories = categoryService.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        return "create-article";
    }

    @PostMapping("/{username}/create-article")
    public String createArticlePost(@ModelAttribute Article article, @PathVariable("username") String username,
                                    @RequestParam("category") Long categoryId, @RequestParam(name = "isPublic") Boolean isPublic) {
        User user = userService.findUserByUsername(username);
        Category category = categoryService.getCategoryById(categoryId);
        article.setCategory(category);
        article.setUser(user);
        article.setCreatedAt();
        article.setPublic(isPublic);
        article.setLikes(0);
        article.setViews(0);
        articleService.saveArticle(article);
        return "redirect:/" + username + "/home";
    }


    @GetMapping("/{username}/edit-article/{id}")
    public String editArticleForm(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        Article article = articleService.getArticleById(id);
        User user = userService.findUserByUsername(username);

        if (article.getUser().getId() != user.getId()) {
            return "redirect:/" + username + "/access-denied";
        }

        List<Category> categories = categoryService.findByUserId(user.getId());
        model.addAttribute("article", article);
        model.addAttribute("categories", categories);
        model.addAttribute("user", user);
        return "edit-article";
    }

    @PostMapping("/{username}/edit-article/{id}")
    public String editArticle(@ModelAttribute Article article, @PathVariable("username") String username, @PathVariable("id") Long articleId,
                              @RequestParam("category") Long categoryId, @RequestParam(name = "isPublic") Boolean isPublic) {
        User user = userService.findUserByUsername(username);
        Article oldArticle = articleService.getArticleById(articleId);
        if (oldArticle.getUser().getId() != user.getId()) {
            return "redirect:/" + username + "/access-denied";
        }
        Category category = categoryService.getCategoryById(categoryId);
        article.setCategory(category);
        article.setUser(user);
        article.setPublic(isPublic);
        articleService.updateArticle(article);
        return "redirect:/" + username + "/background";
    }

    @GetMapping("/{username}/delete-article/{id}")
    public String deleteArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId) {
        Article article = articleService.getArticleById(articleId);
        if (!article.getUser().getUsername().equals(username)) {
            return "redirect:/" + username + "/access-denied";
        }
        articleService.deleteById(articleId);
        return "redirect:/" + username + "/background";
    }

    @GetMapping("/{username}/{id}/like")
    public ResponseEntity<Map<String, Object>> likeArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId) {
        Long userId = userService.findUserByUsername(username).getId();
        if (likeService.getByUserIdAndArticleId(userId, articleId) == null) {
            articleService.updateLikesById(articleId, 1);
            likeService.insertByUserIdAndArticleId(userId, articleId);
        } else {
            articleService.updateLikesById(articleId, -1);
            likeService.deleteByUserIdAndArticleId(userId, articleId);
        }
        int likes = articleService.getArticleById(articleId).getLikes();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("likes", likes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{username}/{id}/favorite")
    public String favoriteArticle(@PathVariable("username") String username, @PathVariable("id") Long articleId,
                                  @RequestParam(name = "favoriteId") Long favoriteId) {
        favoriteArticleService.favoriteArticle(favoriteId, articleId);
        return "redirect:/" + username + "/article/" + articleId;
    }
}
