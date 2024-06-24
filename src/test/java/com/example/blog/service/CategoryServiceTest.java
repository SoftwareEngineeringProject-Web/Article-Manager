package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.Category;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArticleRepository articleRepository;

  @InjectMocks
  private CategoryService categoryService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetCategoryById() {
    Long categoryId = 1L;
    Category expectedCategory = new Category();
    expectedCategory.setId(categoryId);
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

    Category result = categoryService.getCategoryById(categoryId);

    assertEquals(expectedCategory, result);
    verify(categoryRepository, times(1)).findById(categoryId);
  }

  @Test
  public void testFindByUserId() {
    Long userId = 1L;
    List<Category> expectedCategories = new ArrayList<>();
    expectedCategories.add(new Category());
    expectedCategories.add(new Category());
    when(categoryRepository.findByUserId(userId)).thenReturn(expectedCategories);

    List<Category> result = categoryService.findByUserId(userId);

    assertEquals(expectedCategories.size(), result.size());
    verify(categoryRepository, times(1)).findByUserId(userId);
  }

  @Test
  public void testSaveCategory() {
    Category categoryToSave = new Category();
    categoryToSave.setId(1L);
    when(categoryRepository.save(any(Category.class))).thenReturn(categoryToSave);

    Category savedCategory = categoryService.saveCategory(categoryToSave);

    assertEquals(categoryToSave.getId(), savedCategory.getId());
    verify(categoryRepository, times(1)).save(categoryToSave);
  }

  @Test
  public void testDeleteAllCategoryByUserId() {
    Long userId = 1L;
    List<Category> categoriesToDelete = new ArrayList<>();
    Category category1 = new Category(1L, "Category 1", null, 1L);
    Category category2 = new Category(2L, "Category 2", category1, 1L);
    categoriesToDelete.add(category1);
    categoriesToDelete.add(category2);

    when(categoryRepository.findByUserId(userId)).thenReturn(categoriesToDelete);

    categoryService.deleteAllCategoryByUserId(userId);

    verify(categoryRepository, times(1)).findByUserId(userId);
  }

  @Test
  public void testDeleteCategory() {
    Long categoryId = 1L;
    Category categoryToDelete = new Category();
    categoryToDelete.setId(categoryId);

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryToDelete));

    List<Category> childCategories = new ArrayList<>();
    childCategories.add(new Category());
    when(categoryRepository.findByParentId(categoryId)).thenReturn(childCategories);

    doNothing().when(articleRepository).setCategoryIdToNullByCategoryId(anyLong());

    doAnswer(invocation -> {
      Long idToDelete = invocation.getArgument(0);
      assertEquals(categoryId, idToDelete);
      return null;
    }).when(categoryRepository).deleteById(anyLong());

    categoryService.deleteCategory(categoryId);

    verify(categoryRepository, times(1)).findById(categoryId);
    verify(categoryRepository, times(1)).findByParentId(categoryId);
    verify(articleRepository, times(1)).setCategoryIdToNullByCategoryId(categoryId);
    verify(categoryRepository, times(1)).deleteById(anyLong());
  }

  @Test
  public void testGetCategoryTree() {
    String username = "testUser";
    Long categoryId = 1L;
    int page = 0;

    User mockUser = new User();
    mockUser.setId(1L);
    when(userRepository.findByUsername(username)).thenReturn(mockUser);

    List<Category> mockCategories = new ArrayList<>();
    Category category1 = new Category();
    category1.setId(1L);
    category1.setName("Category 1");
    mockCategories.add(category1);
    when(categoryRepository.findByUserId(mockUser.getId())).thenReturn(mockCategories);

    PageRequest pageable = PageRequest.of(page, 10);
    Article article1 = new Article(1L, null, null, mockUser, category1);
    Article article2 = new Article(2L, null, null, mockUser, category1);
    Page<Article> articlePage = new PageImpl<>(Arrays.asList(article1, article2), pageable, 2);
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category1));
    when(articleRepository.findByUserIdAndCategory(mockUser.getId(), category1, pageable)).thenReturn(articlePage);

    Map<String, Object> result = categoryService.getCategoryTree(username, categoryId, page);

    assertEquals(articlePage.getContent(), result.get("articles"));
    assertNotNull(result.get("rootCategory"));
    assertEquals(mockUser, result.get("user"));
    assertEquals(categoryId, result.get("currentCategoryId"));
    assertEquals(page, result.get("currentPage"));
    assertEquals(1, result.get("totalPages"));
  }


}
