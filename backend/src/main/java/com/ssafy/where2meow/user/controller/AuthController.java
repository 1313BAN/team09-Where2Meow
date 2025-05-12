package com.ssafy.where2meow.user.controller;

import com.ssafy.where2meow.user.dto.LoginRequest;
import com.ssafy.where2meow.user.dto.LoginResponse;
import com.ssafy.where2meow.user.service.UserService;
import com.ssafy.where2meow.user.token.JwtTokenProvider;
import com.ssafy.where2meow.user.token.TokenBlacklist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final UserService userService;
  private final LoginCookie loginCookie;
  private final JwtTokenProvider jwtTokenProvider;
  private final TokenBlacklist tokenBlacklist;

  /**
   * 로그인 처리
   *
   * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호, 아이디 저장, 자동 로그인)
   * @param response     HTTP 응답
   * @return JWT 토큰 및 사용자 정보
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
    LoginResponse loginResponse = userService.login(loginRequest);

    // 아이디 저장 쿠키 처리 (서버에서 쿠키 생성/삭제 수행)
    loginCookie.setEmailCookie(loginRequest.getEmail(), loginRequest.isRememberEmail(), response);

    return ResponseEntity.ok(loginResponse);
  }

  /**
   * 로그아웃 처리
   *
   * @param request HTTP 요청
   * @return 성공 응답 또는 오류 응답
   */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    try {
      // 요청 헤더에서 토큰 추출
      String token = jwtTokenProvider.resolveToken(request);

      if (token == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // 포함된 토큰을 저장
      String currentToken = token;

      // 이미 블랙리스트에 있는 토큰인지 확인
      boolean isBlacklisted = tokenBlacklist.isBlacklisted(token);

      if (isBlacklisted) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

      try {
        userService.logout();

        // 토큰이 블랙리스트에 추가되었는지 다시 확인
        boolean isNowBlacklisted = tokenBlacklist.isBlacklisted(currentToken);

        // 블랙리스트에 추가되지 않았다면 강제 추가
        if (!isNowBlacklisted) {
          long expirationTime = jwtTokenProvider.getExpirationTime(currentToken);
          tokenBlacklist.addToBlacklist(currentToken, expirationTime);
        }

        return ResponseEntity.ok().build();

      } catch (Exception e) {
        e.printStackTrace();

        // 오류가 발생해도 토큰을 블랙리스트에 추가
        try {
          long expirationTime = jwtTokenProvider.getExpirationTime(currentToken);
          tokenBlacklist.addToBlacklist(currentToken, expirationTime);
        } catch (Exception ex) {
          log.error("[로그아웃] 직접 추가 실패: {}", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }

    } catch (Exception e) {
      // 인증 토큰이 없거나 유효하지 않은 경우 403 Forbidden 반환
      log.error("[로그아웃] 오류 발생: {}", e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}