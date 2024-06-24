package com.example.blog.service;

import com.example.blog.entity.Article;
import com.example.blog.entity.User;
import com.example.blog.entity.relation.Like;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.LikeRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

;

public class LikeServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ArticleRepository articleRepository;

  @Mock
  private LikeRepository likeRepository;

  @InjectMocks
  private LikeService likeService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testLikeArticle() {
    Long articleId = 1L;
    String username = "testUser";
    User mockUser = new User();
    mockUser.setId(1L);
    Article mockArticle = new Article();
    mockArticle.setId(articleId);
    mockArticle.setLikes(0);

    when(userRepository.findByUsername(username)).thenReturn(mockUser);
    when(articleRepository.findById(articleId)).thenReturn(Optional.of(mockArticle));
    when(likeRepository.findByUserIdAndArticleId(mockUser.getId(), articleId)).thenReturn(null);
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        mockArticle.setLikes(mockArticle.getLikes() + 1);
        return null;
      }
    }).when(articleRepository).updateLikesById(articleId, 1);
    Integer likeCount = likeService.getLikeCountByArticleId(articleId, username);

    assertEquals(1, likeCount);
    verify(articleRepository, times(1)).updateLikesById(articleId, 1);
    verify(likeRepository, times(1)).insertByUserIdAndArticleId(mockUser.getId(), articleId);
  }

  @Test
  public void testUnlikeArticle() {
    Long articleId = 1L;
    String username = "testUser";
    User mockUser = new User();
    mockUser.setId(1L);
    Article mockArticle = new Article();
    mockArticle.setId(articleId);
    mockArticle.setLikes(1);

    when(userRepository.findByUsername(username)).thenReturn(mockUser);
    when(articleRepository.findById(articleId)).thenReturn(Optional.of(mockArticle));
    Like mockLike = new Like();
    when(likeRepository.findByUserIdAndArticleId(mockUser.getId(), articleId)).thenReturn(mockLike);
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        mockArticle.setLikes(mockArticle.getLikes() - 1);
        return null;
      }
    }).when(articleRepository).updateLikesById(articleId, -1);
    Integer likeCount = likeService.getLikeCountByArticleId(articleId, username);

    assertEquals(0, likeCount);
    verify(articleRepository, times(1)).updateLikesById(articleId, -1);
    verify(likeRepository, times(1)).deleteByUserIdAndArticleId(mockUser.getId(), articleId);
  }

  @Test
  public void testArticleNotFound() {
    Long articleId = 1L;
    String username = "testUser";
    User mockUser = new User();
    mockUser.setId(1L);

    when(userRepository.findByUsername(username)).thenReturn(mockUser);
    when(articleRepository.findById(articleId)).thenReturn(java.util.Optional.empty());

    Integer likeCount = likeService.getLikeCountByArticleId(articleId, username);

    assertNull(likeCount);
    verify(articleRepository, times(0)).updateLikesById(anyLong(), anyInt());
    verify(likeRepository, times(0)).insertByUserIdAndArticleId(anyLong(), anyLong());
    verify(likeRepository, times(0)).deleteByUserIdAndArticleId(anyLong(), anyLong());
  }
}
