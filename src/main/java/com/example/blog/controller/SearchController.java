package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.SearchService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;

@Controller
public class SearchController {
  @Autowired
  private UserService userService;
  @Autowired
  private SearchService searchService;

  @GetMapping("/{username}/search-articles")
  public String showSearchArticlesPage(@PathVariable("username") String username, Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    return "search-articles";
  }

  @PostMapping("/{username}/search-articles")
  public String searchArticles(@PathVariable("username") String username,
                               @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(name = "keyword") String keyword,
                               @RequestParam(name = "beginDate") String beginDate,
                               @RequestParam(name = "endDate") String endDate,
                               Model model) {
    User user = userService.findUserByUsername(username);
    model.addAttribute("user", user);
    if (keyword.isEmpty() && beginDate.isEmpty() && endDate.isEmpty()) {
      return "search-articles";
    }
    Instant begin;
    Instant end;
    assert beginDate != null;
    begin = beginDate.isEmpty() ? Instant.EPOCH : Instant.parse(beginDate +":00.00Z");
    end = endDate.isEmpty() ? Instant.now() : Instant.parse(endDate+ ":00.00Z");
    model.addAllAttributes(searchService.searchArticle(user.getId(), pageNo, keyword, begin, end));
    return "search-articles";
  }
}
