package com.ssafy.where2meow.user.service;

import com.ssafy.where2meow.exception.LogoutException;
import com.ssafy.where2meow.user.dto.*;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import com.ssafy.where2meow.user.token.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 로그인 처리
   *
   * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
   * @return 로그인 응답 (토큰, 사용자 정보)
   * @throws RuntimeException 인증 실패 시 발생
   */
  public LoginResponse login(LoginRequest loginRequest) {
    try {
      // 인증 시도
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getPassword()
          )
      );

      // 인증 정보 저장
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // 사용자 정보 조회 - 활성 상태인 사용자만 조회
      User user = userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail())
          .orElseThrow(() -> new UsernameNotFoundException(loginRequest.getEmail() + "과 일치하는 사용자가 없습니다."));

      // JWT 토큰 생성 (rememberMe 값 적용)
      String token = jwtTokenProvider.createToken(
          user.getUuid(),
          user.getRole().name(),
          loginRequest.isRememberMe() // rememberMe 값 전달
      );

      return LoginResponse.builder()
          .token(token)
          .uuid(user.getUuid().toString())
          .name(user.getName())
          .email(user.getEmail())
          .role(user.getRole().name())
          .build();

    } catch (BadCredentialsException e) {
      throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.", e);
    } catch (LockedException e) { // 실제로 발생은 안함
      throw new RuntimeException("계정이 잠겨 있습니다.", e);
    } catch (DisabledException e) { // findByEmailAndIsActiveTrue로 조회하기 때문에 발생은 안함
      throw new RuntimeException("계정이 비활성화되어 있습니다.", e);
    } catch (AuthenticationException e) {
      throw new RuntimeException("인증에 실패했습니다: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.", e);
    }
  }

//  /**
//   * 토큰을 사용하여 자동 로그인 처리
//   *
//   * @param token 자동 로그인용 토큰
//   * @return 로그인 응답 (토큰, 사용자 정보)
//   * @throws RuntimeException 토큰 검증 실패 시 발생
//   */
//  public LoginResponse loginWithToken(String token) {
//    // 토큰 유효성 검사
//    if (!jwtTokenProvider.validateToken(token)) {
//      throw new RuntimeException("유효하지 않은 또는 만료된 토큰입니다.");
//    }
//
//    // 토큰에서 사용자 이메일 가져오기
//    String email = jwtTokenProvider.getUsername(token);
//
//    // 사용자 정보 조회
//    User user = userRepository.findByEmailAndIsActiveTrue(email)
//        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
//
//    // 인증 객체 생성 및 설정
//    Authentication authentication = jwtTokenProvider.getAuthentication(token);
//    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//    return LoginResponse.builder()
//        .token(token) // 기존 토큰 재사용
//        .uuid(user.getUuid().toString())
//        .name(user.getName())
//        .email(user.getEmail())
//        .role(user.getRole().name())
//        .build();
//  }

  /**
   * 로그아웃 처리
   * 현재 사용 중인 토큰을 블랙리스트에 추가하여 무효화
   *
   * @param request HTTP 요청 객체
   * @throws LogoutException 토큰이 없거나 검증에 실패한 경우 발생
   */
  public void logout(HttpServletRequest request) {
    // 요청 헤더에서 토큰 추출
    String token = jwtTokenProvider.resolveToken(request);

    if (token == null) {
      throw new LogoutException("인증 토큰이 없습니다.");
    }

    // 이미 블랙리스트에 있는 토큰인지 확인
    if (jwtTokenProvider.isTokenBlacklisted(token)) {
      throw new LogoutException("이미 로그아웃 처리된 토큰입니다.");
    }

    // 토큰 유효성 검사
    if (!jwtTokenProvider.validateToken(token)) {
      throw new LogoutException("유효하지 않은 토큰입니다.");
    }

    try {
      // 토큰을 블랙리스트에 추가
      jwtTokenProvider.blacklistToken(token);

      // 현재 인증 컨텍스트 초기화
      SecurityContextHolder.clearContext();
    } catch (Exception e) {
      throw new LogoutException("토큰 블랙리스트 처리 오류: " + e.getMessage(), e);
    }
  }

  public String findId(FIndIdRequest fIndIdRequest) {
    User user = userRepository.findByNameAndPhone(fIndIdRequest.getName(), fIndIdRequest.getPhone())
        .orElseThrow(() -> new UsernameNotFoundException("일치하는 유저가 없습니다."));

    return user.getEmail();
  }

  public void checkUser(ResetPasswordCheckRequest checkRequest) {
    User user = userRepository.findByEmailAndIsActiveTrue(checkRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("일치하는 유저가 없습니다."));
    if (!user.getName().equals(checkRequest.getName())) {
      throw new IllegalArgumentException("사용자 이름이 일치하지 않습니다.");
    }
  }

  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    // 비밀번호와 확인 비밀번호가 일치하는지 확인
    if (!resetPasswordRequest.getPassword().equals(resetPasswordRequest.getConfirmPassword())) {
      throw new IllegalArgumentException("비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }

    // 비밀번호 복잡도 검증
    PasswordComplexityUtil.validatePasswordComplexity(resetPasswordRequest.getPassword());

    User user = userRepository.findByEmailAndIsActiveTrue(resetPasswordRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("일치하는 유저가 없습니다."));

    // 이전 비밀번호와 동일한지 확인
    if (passwordEncoder.matches(resetPasswordRequest.getPassword(), user.getPassword())) {
      throw new IllegalArgumentException("이 비밀번호로 변경할 수 없습니다.");
    }

    // 비밀번호 인코딩 후 저장
    String encodedPassword = passwordEncoder.encode(resetPasswordRequest.getPassword());
    user.setPassword(encodedPassword);
    userRepository.save(user);
  }
}
