package com.ssafy.where2meow.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Optional;

/**
 * 쿠키 관련 관리 클래스
 */
@Component
public class LoginCookie {

    private static final String EMAIL_COOKIE_NAME = "remembered_email";
    private static final int EMAIL_COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30일
    private static final String COOKIE_PATH = "/";
    
    /**
     * 이메일 쿠키를 생성하거나 삭제합니다.
     * 
     * @param email 저장할 이메일 주소
     * @param rememberEmail true면 쿠키 생성, false면 쿠키 삭제
     * @param response HTTP 응답 객체
     */
    public void setEmailCookie(String email, boolean rememberEmail, HttpServletResponse response) {
        Cookie cookie = new Cookie(EMAIL_COOKIE_NAME, email);
        cookie.setPath(COOKIE_PATH);
        
        if (rememberEmail) {
            cookie.setMaxAge(EMAIL_COOKIE_MAX_AGE);
        } else {
            cookie.setMaxAge(0); // 쿠키 삭제
        }
        
        response.addCookie(cookie);
    }
}