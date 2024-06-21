package com.example.blog.controller;

import com.example.blog.entity.User;
import com.example.blog.service.SearchService;
import com.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/{username}/search-article-list")
  public String searchArticles(@PathVariable("username") String username,
                               @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(name = "keywordInTitle") String keywordInTitle,
                               @RequestParam(name = "keywordInContent") String keywordInContent,
                               @RequestParam(name = "beginDate") String beginDate,
                               @RequestParam(name = "endDate") String endDate,
                               @RequestParam(name = "beginDateDummy") String beginDateDummy,
                               @RequestParam(name = "endDateDummy") String endDateDummy,
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
    model.addAttribute("beginDateDummy", beginDateDummy);
    model.addAttribute("endDateDummy", endDateDummy);
    return "search-articles";
  }
}
