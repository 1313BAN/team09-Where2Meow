package com.ssafy.where2meow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//  @Bean
//  ServletListenerRegistrationBean<ServletContextListener> initListener() {
//    return null;
//  }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }
}