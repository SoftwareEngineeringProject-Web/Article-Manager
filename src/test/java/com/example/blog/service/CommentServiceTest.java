package com.example.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;

public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCommentById() {
        Long commentId = 1L;
        Comment comment = new Comment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(commentId);

        assertEquals(comment, result);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    public void testDeleteComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setArticleId(2L);

        commentService.deleteComment(comment);

        verify(articleRepository, times(1)).updateCommentsById(comment.getArticleId(), -1);
        verify(commentRepository, times(1)).deleteById(comment.getId());
    }

    @Test
    public void testSubmitComment() {
        String username = "testuser";
        Long articleId = 1L;
        String commentText = "This is a test comment";

        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        commentService.submitComment(username, articleId, commentText);

        verify(articleRepository, times(1)).updateCommentsById(articleId, 1);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
