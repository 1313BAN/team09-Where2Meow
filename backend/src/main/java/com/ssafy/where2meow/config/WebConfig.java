package com.ssafy.where2meow.config;

import jakarta.servlet.ServletContextListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@MapperScan("com.ssafy.where2meow.**.repository")
public class WebConfig implements WebMvcConfigurer {

  @Bean
  ServletListenerRegistrationBean<ServletContextListener> initListener() {
    return null;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
  }
}