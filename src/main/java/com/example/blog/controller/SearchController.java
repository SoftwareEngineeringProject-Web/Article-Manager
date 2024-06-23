package com.example.blog.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.view.RedirectView;

import com.example.blog.entity.User;
import com.example.blog.service.SearchService;
import com.example.blog.service.UserService;

@Controller
public class SearchController {
  @Autowired
  private UserService userService;
  @Autowired
  private SearchService searchService;

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

  @GetMapping("/{username}/search-articles")
  public String searchArticles(@PathVariable("username") String username,
                               @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(name = "keywordInTitle", defaultValue = "") String keywordInTitle,
                               @RequestParam(name = "keywordInContent", defaultValue = "") String keywordInContent,
                               @RequestParam(name = "beginDate", defaultValue = "") String beginDate,
                               @RequestParam(name = "endDate", defaultValue = "") String endDate,
                               @RequestParam(name = "beginDateDummy", defaultValue = "") String beginDateLocale,
                               @RequestParam(name = "endDateDummy", defaultValue = "") String endDateLocale,
                               Model model) {
    beginDate = beginDate.isEmpty() ? null : beginDate;
    endDate = endDate.isEmpty() ? null : endDate;
    keywordInTitle = keywordInTitle.isEmpty() ? null : keywordInTitle;
    keywordInContent = keywordInContent.isEmpty() ? null : keywordInContent;

    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    if (keywordInTitle == null && beginDate == null &&
        endDate == null && keywordInContent == null) {
      return "search-articles";
    }
    Instant begin = Instant.parse(beginDate == null ? "2000-01-01T00:00:00Z" : beginDate);
    Instant end = endDate == null ? Instant.now() : Instant.parse(endDate);
    model.addAllAttributes(searchService.searchArticle(user.getId(), pageNo, keywordInTitle, keywordInContent, begin, end));
    model.addAttribute("beginDateDummy", beginDateLocale);
    model.addAttribute("endDateDummy", endDateLocale);
    return "search-articles";
  }
}
