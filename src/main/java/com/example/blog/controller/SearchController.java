package com.example.blog.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.entity.User;
import com.example.blog.service.SearchService;
import com.example.blog.service.UserService;

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
    if (keyword == null && beginDate == null && endDate == null) {
      return "search-articles";
    }
    Instant begin = null;
    Instant end = null;
    if (beginDate != null || endDate != null) {
      begin = Instant.parse(beginDate == null ? "2000-01-01T00:00:00Z" : beginDate);
      end = endDate == null ? Instant.now() : Instant.parse(endDate);
    }
    model.addAllAttributes(searchService.searchArticle(pageNo, keyword, begin, end));
    return "search-articles";
  }
}
