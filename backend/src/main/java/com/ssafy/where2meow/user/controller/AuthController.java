package com.ssafy.where2meow.user.controller;

import com.ssafy.where2meow.exception.LogoutException;
import com.ssafy.where2meow.user.dto.*;
import com.ssafy.where2meow.user.service.AuthService;
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

  private final AuthService authService;
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
    LoginResponse loginResponse = authService.login(loginRequest);

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
    // 요청 헤더에서 토큰 추출
    String token = jwtTokenProvider.resolveToken(request);

    if (token == null) {
      throw new LogoutException("인증 토큰이 없습니다");
    }

    // 이미 블랙리스트에 있는 토큰인지 확인
    boolean isBlacklisted = tokenBlacklist.isBlacklisted(token);

    if (isBlacklisted) {
      throw new LogoutException("이미 로그아웃 처리된 토큰입니다");
    }

    // 로그아웃 서비스 호출
    authService.logout();

    // 토큰이 블랙리스트에 추가되었는지 다시 확인
    boolean isNowBlacklisted = tokenBlacklist.isBlacklisted(token);

    // 블랙리스트에 추가되지 않았다면 강제 추가
    if (!isNowBlacklisted) {
      try {
        long expirationTime = jwtTokenProvider.getExpirationTime(token);
        tokenBlacklist.addToBlacklist(token, expirationTime);
      } catch (Exception ex) {
        throw new LogoutException("토큰 블랙리스트 추가 실패: " + ex.getMessage(), ex);
      }
    }

    return ResponseEntity.ok().build();
  }

  @PostMapping("/find-id")
  public ResponseEntity<String> findId(@RequestBody @Valid FIndIdRequest fIndIdRequest) {
    String userId = authService.findId(fIndIdRequest);
    return ResponseEntity.ok(userId);
  }

  @PostMapping("/password/check")
  public ResponseEntity<Void> userCheck(@RequestBody @Valid ResetPasswordCheckRequest checkRequest) {
    if(authService.checkUser(checkRequest)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  @PutMapping("/password/reset")
  public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
    if(authService.resetPassword(resetPasswordRequest)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }
}