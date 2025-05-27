package com.ssafy.where2meow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://127.0.0.1:5173", "http://127.0.0.1:8000",
            "http://localhost:5173", "http://localhost:8000")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
        .allowedHeaders("*")  // 모든 헤더 허용
        .allowCredentials(true)
        .maxAge(3600);  // preflight 캐시 시간
  }
}
