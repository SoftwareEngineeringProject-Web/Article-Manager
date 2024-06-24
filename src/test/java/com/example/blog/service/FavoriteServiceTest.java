package com.example.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.blog.entity.Favorite;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.FavoriteArticleRepository;
import com.example.blog.repository.FavoriteRepository;

class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private FavoriteArticleRepository favoriteArticleRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFavoriteById() {
        Long favoriteId = 1L;
        Favorite expectedFavorite = new Favorite(1L, "Test Favorite");
        expectedFavorite.setId(favoriteId);

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(expectedFavorite));

        Favorite actualFavorite = favoriteService.getFavoriteById(favoriteId);

        assertNotNull(actualFavorite);
        assertEquals(expectedFavorite, actualFavorite);
    }

    @Test
    void testUpdate() {
        Favorite existingFavorite = new Favorite(1L, "Test Favorite");
        existingFavorite.setId(1L);

        favoriteService.update(existingFavorite);

        verify(favoriteRepository, times(1)).save(existingFavorite);
    }

    @Test
    void testDeleteFavoriteByFavoriteId() {
        Long favoriteId = 1L;
        Long articleId1 = 2L;
        Long articleId2 = 3L;

        when(favoriteArticleRepository.findArticleIdsByFavoriteId(favoriteId)).thenReturn(Arrays.asList(articleId1, articleId2));

        favoriteService.deleteFavoriteByFavoriteId(favoriteId);

        verify(favoriteRepository, times(1)).deleteById(favoriteId);
        verify(articleRepository, times(1)).updateFavoritesById(articleId1, -1);
        verify(articleRepository, times(1)).updateFavoritesById(articleId2, -1);
    }

    @Test
    void testAddFavorite() {
        Long userId = 1L;
        String favoriteName = "Test Favorite";

        ArgumentCaptor<Favorite> captor = ArgumentCaptor.forClass(Favorite.class);
        Favorite savedFavorite = favoriteService.addFavorite(userId, favoriteName);

        verify(favoriteRepository, times(1)).save(captor.capture());
        Favorite actualFavorite = captor.getValue();

        assertNotNull(actualFavorite);
        assertEquals(savedFavorite.getUserId(), actualFavorite.getUserId());
        assertEquals(savedFavorite.getName(), actualFavorite.getName());
        verify(favoriteRepository, times(1)).save(savedFavorite);
    }
}
