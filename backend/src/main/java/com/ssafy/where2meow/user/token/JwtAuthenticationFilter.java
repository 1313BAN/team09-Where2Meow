package com.ssafy.where2meow.user.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

//    // 인증이 필요한 엔드포인트인지 체크하는 메서드
//    if (isSecureEndpoint(request)) {
//      String token = jwtTokenProvider.resolveToken(request);
//
//      // 토큰이 없는 경우
//      if (token == null) {
//        handleAuthenticationFailure(response, "인증 토큰이 필요합니다");
//        return; // 중요: 여기서 필터 체인 중단
//      }
//
//      // 토큰 검증
//      try {
//        if (!jwtTokenProvider.validateToken(token)) {
//          handleAuthenticationFailure(response, "유효하지 않은 토큰입니다");
//          return; // 중요: 여기서 필터 체인 중단
//        }
//
//        // 토큰이 유효한 경우 인증 정보 설정
//        Authentication auth = jwtTokenProvider.getAuthentication(token);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//      } catch (Exception e) {
//        handleAuthenticationFailure(response, e.getMessage());
//        return; // 중요: 필터 체인 중단
//      }
//    }

    String token = jwtTokenProvider.resolveToken(request);

    // 토큰이 있는 경우만 검증 (토큰이 없으면 그냥 통과)
    if (token != null) {
      try {
        if (jwtTokenProvider.validateToken(token)) {
          // 토큰이 유효한 경우 인증 정보 설정
          Authentication auth = jwtTokenProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception e) {
        // 토큰 검증 실패 로깅만 하고 계속 진행
        logger.debug("JWT 토큰 검증 실패: " + e.getMessage());
      }
    }

    // 인증이 필요 없는 엔드포인트이거나 인증이 성공한 경우에만 여기에 도달
    filterChain.doFilter(request, response);
  }

//  // 인증이 필요한 엔드포인트인지 확인하는 메서드
//  private boolean isSecureEndpoint(HttpServletRequest request) {
//    String path = request.getRequestURI();
//    String method = request.getMethod();
//
//    // SecurityConfig와 일치하는 패턴으로 설정
//    return path.startsWith("/api/plan/user") ||
//        (path.startsWith("/api/plan/") &&
//            (method.equals("POST") || method.equals("PUT") || method.equals("DELETE"))) ||
//        path.startsWith("/api/user") ||
//        path.equals("/api/auth/logout");
//  }

  // 인증 실패 처리 메서드
  private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");

    // 표준화된 오류 응답 형식
    response.setCharacterEncoding("UTF-8");
    String errorJson = String.format("{\"message\":\"%s\"}", message);
    response.getWriter().write(errorJson);
  }
}
