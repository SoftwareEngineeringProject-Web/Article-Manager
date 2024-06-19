package com.example.blog.controller;

import com.example.blog.entity.Comment;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class CommentController {
  @Autowired
  UserService userService;
  @Autowired
  ArticleService articleService;
  @Autowired
  CommentService commentService;

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

  @PostMapping("/{username}/submit-comment/{articleId}")
  public String submitComment(@PathVariable("username") String username, @PathVariable("articleId") Long articleId,
                              @RequestParam("comment") String comment) {
    commentService.submitComment(username, articleId, comment);
    return "redirect:/" + username + "/article/" + articleId;
  }

  @DeleteMapping("/{username}/delete-comment/{commentId}")
  public ResponseEntity<String> deleteComment(@PathVariable("username") String username, @PathVariable("commentId") Long commentId, Model model) {
    Comment comment = commentService.getCommentById(commentId);
    model.addAttribute("defaultPage", "manage-comments");
    if (comment.getArticle().getUser().getUsername().equals(username)
        || comment.getUser().getUsername().equals(username)) {
      commentService.deleteComment(comment);
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无权访问");
    }
    return ResponseEntity.ok("删除成功");
  }
}
