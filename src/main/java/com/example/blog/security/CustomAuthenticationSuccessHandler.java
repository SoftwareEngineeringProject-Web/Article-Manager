package com.example.blog.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    // 获取用户详细信息
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    // 获取用户名
    String username = userDetails.getUsername();

    // 构建重定向URL，将 {username} 替换为当前用户的实际标识符
    String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/{username}/home")
        .buildAndExpand(username)
        .toUriString();

    // 重定向
    response.sendRedirect(redirectUrl);

  }
}
