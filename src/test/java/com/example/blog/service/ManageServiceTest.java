package com.example.blog.service;

import com.example.blog.data.FavoriteWithArticles;
import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Favorite;
import com.example.blog.entity.User;
import com.example.blog.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ManageServiceTest {

    @InjectMocks
    private ManageService manageService;

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private FavoriteArticleRepository favoriteArticleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSetArticlesInformationAll() {
        Long userId = 1L;
        Long categoryId = 1L;
        String title = "Test Article";
        int page = 0;

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(categoryId, "Category 1", null, userId));

        Page<Article> articlePages = new PageImpl<>(new ArrayList<>());

        when(categoryRepository.findByUserId(userId)).thenReturn(categories);
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categories.get(0)));
        when(articleRepository.findByUserIdAndTitleAndCategory(userId, title, categories.get(0), PageRequest.of(page, 10))).thenReturn(articlePages);

        Map<String, Object> articlesData = manageService.setArticlesInformation(userId, categoryId, title, page);

        assertEquals(articlePages.getContent(), articlesData.get("articles"));
        assertEquals(page, articlesData.get("currentPage"));
        assertEquals(articlePages.getTotalPages(), articlesData.get("totalPages"));
        assertEquals(categories, articlesData.get("categories"));
    }

    @Test
    void testSetArticlesInformationWithNullCategoryId() {
        Long userId = 1L;
        Long categoryId = null;
        String title = "Test Article";
        int page = 0;

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(categoryId, "Category 1", null, userId));

        Page<Article> articlePages = new PageImpl<>(new ArrayList<>());

        when(categoryRepository.findByUserId(userId)).thenReturn(categories);
        when(articleRepository.findByUserIdAndTitle(userId, title, PageRequest.of(page, 10))).thenReturn(articlePages);

        Map<String, Object> articlesData = manageService.setArticlesInformation(userId, categoryId, title, page);

        assertEquals(articlePages.getContent(), articlesData.get("articles"));
        assertEquals(page, articlesData.get("currentPage"));
        assertEquals(articlePages.getTotalPages(), articlesData.get("totalPages"));
        assertEquals(categories, articlesData.get("categories"));
    }

    @Test
    void testSetArticlesInformationWithNullTitle() {
        Long userId = 1L;
        Long categoryId = 1L;
        String title = null;
        int page = 0;

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(categoryId, "Category 1", null, userId));

        Page<Article> articlePages = new PageImpl<>(new ArrayList<>());

        when(categoryRepository.findByUserId(userId)).thenReturn(categories);
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(categories.get(0)));
        when(articleRepository.findByUserIdAndCategory(userId, categories.get(0), PageRequest.of(page, 10))).thenReturn(articlePages);

        Map<String, Object> articlesData = manageService.setArticlesInformation(userId, categoryId, title, page);

        assertEquals(articlePages.getContent(), articlesData.get("articles"));
        assertEquals(page, articlesData.get("currentPage"));
        assertEquals(articlePages.getTotalPages(), articlesData.get("totalPages"));
        assertEquals(categories, articlesData.get("categories"));
    }

    @Test
    void testSetArticlesInformationWithAllNull() {
        Long userId = 1L;
        Long categoryId = null;
        String title = null;
        int page = 0;

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(categoryId, "Category 1", null, userId));

        Page<Article> articlePages = new PageImpl<>(new ArrayList<>());

        when(categoryRepository.findByUserId(userId)).thenReturn(categories);
        when(articleRepository.findPagedByUserId(userId, PageRequest.of(page, 10))).thenReturn(articlePages);

        Map<String, Object> articlesData = manageService.setArticlesInformation(userId, categoryId, title, page);

        assertEquals(articlePages.getContent(), articlesData.get("articles"));
        assertEquals(page, articlesData.get("currentPage"));
        assertEquals(articlePages.getTotalPages(), articlesData.get("totalPages"));
        assertEquals(categories, articlesData.get("categories"));
    }

    @Test
    void testSetCommentsInformation() {
        Long userId = 1L;
        int page = 0;

        Page<Comment> commentPages = new PageImpl<>(new ArrayList<>());

        when(commentRepository.findPagedByUserId(userId, PageRequest.of(page, 10))).thenReturn(commentPages);

        Map<String, Object> commentsData = manageService.setCommentsInformation(userId, page);

        assertEquals(commentPages.getContent(), commentsData.get("comments"));
        assertEquals(page, commentsData.get("currentPage"));
        assertEquals(commentPages.getTotalPages(), commentsData.get("totalPages"));
    }

    @Test
    void testSetFavoritesInformation() {
        Long userId = 1L;

        List<Favorite> favorites = new ArrayList<>();
        favorites.add(new Favorite(1L, userId, "Favorite 1"));
        favorites.add(new Favorite(2L, userId, "Favorite 2"));

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "Category 1", null, userId));
        User user = new User(userId, "user", null, null, null);

        List<Article> favoriteArticles1 = new ArrayList<>();
        favoriteArticles1.add(new Article(1L, "Article 1", "Content 1", user, categories.get(0)));
        favoriteArticles1.add(new Article(2L, "Article 2", "Content 2", user, categories.get(0)));

        List<Article> favoriteArticles2 = new ArrayList<>();
        favoriteArticles2.add(new Article(3L, "Article 3", "Content 3", user, categories.get(0)));

        when(favoriteRepository.findByUserId(userId)).thenReturn(favorites);
        when(favoriteArticleRepository.findArticlesByFavoriteId(favorites.get(0).getId())).thenReturn(favoriteArticles1);
        when(favoriteArticleRepository.findArticlesByFavoriteId(favorites.get(1).getId())).thenReturn(favoriteArticles2);

        ArrayList<FavoriteWithArticles> favoriteWithArticlesList = manageService.setFavoritesInformation(userId);

        assertEquals(2, favoriteWithArticlesList.size());
        assertEquals(favorites.get(0), favoriteWithArticlesList.get(0).getFavorite());
        assertEquals(favoriteArticles1, favoriteWithArticlesList.get(0).getArticles());
        assertEquals(favorites.get(1), favoriteWithArticlesList.get(1).getFavorite());
        assertEquals(favoriteArticles2, favoriteWithArticlesList.get(1).getArticles());
    }
}
