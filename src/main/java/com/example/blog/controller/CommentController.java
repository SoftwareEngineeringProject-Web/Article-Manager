package com.example.blog.controller;

import com.example.blog.entity.Article;
import com.example.blog.entity.Comment;
import com.example.blog.entity.User;
import com.example.blog.service.ArticleService;
import com.example.blog.service.CommentService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

  @PostMapping("/{username}/{id}/submit-comment")
  public String submitComment(@PathVariable("username") String username, @PathVariable("id") Long articleId,
                              @RequestParam("comment") String comment) {
    User user = userService.findUserByUsername(username);
    Article article = articleService.getArticleById(articleId);
    Comment commentEntity = new Comment(user, article, comment, null);
    commentService.saveComment(commentEntity);
    return "redirect:/" + username + "/article/" + articleId;
  }

  @GetMapping("/{username}/{id}/delete-comment")
  public String deleteComment(@PathVariable("username") String username, @PathVariable("id") Long commentId, Model model) {
    User user = userService.findUserByUsername(username);
    Comment comment = commentService.getCommentById(commentId);
    model.addAttribute("defaultPage", "manage-comments");
    if (comment.getArticle().getUser().getUsername().equals(username)
        || comment.getUser().getUsername().equals(username)) {
      commentService.deleteCommentById(commentId);
    } else {
      return "redirect:/" + username + "/access-denied";
    }
    return "redirect:/" + username + "/background";
  }
}