package com.codeit.monew.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final HeaderAuthenticationFilter headerAuthenticationFilter;

  public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter) {
    this.headerAuthenticationFilter = headerAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                org.springframework.boot.security.autoconfigure.web.servlet.PathRequest.toH2Console())
            .permitAll()
            .requestMatchers(
                "/",
                "/error",
                "/index.html",
                "/favicon.ico",
                "/assets/**",
                "/fonts/**",
                "/api/test/**",
                "/api/articles/**",
                "/api/interests/**",
                "/api/auth/**",
                "/api/user-activities/**",
                "/api/users",
                "/api/users/**")
            .permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/comments")
            .permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/comments")
            .permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/comments/**")
            .permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(headerAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
        .formLogin(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
