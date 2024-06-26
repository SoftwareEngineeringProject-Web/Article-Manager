package com.example.blog.config;

import com.example.blog.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            .disable()
        )
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/admin-login","/register", "/login", "/Css/**", "js/**", "images/**").permitAll()
                .anyRequest().authenticated()
        )
        .formLogin(formLogin ->
            formLogin
                .loginPage("/login").permitAll()
                .successHandler(new CustomAuthenticationSuccessHandler()) // 使用自定义的成功处理程序
                .failureUrl("/login?error=true")
        ).logout(logout ->
            logout.permitAll()
        );
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
