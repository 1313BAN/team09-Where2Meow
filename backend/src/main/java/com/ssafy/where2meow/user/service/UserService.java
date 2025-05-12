package com.ssafy.where2meow.user.service;

import com.ssafy.where2meow.user.dto.LoginRequest;
import com.ssafy.where2meow.user.dto.LoginResponse;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import com.ssafy.where2meow.user.token.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final HttpServletRequest request;

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
          .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

      // JWT 토큰 생성
      String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

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

  /**
   * 로그아웃 처리
   * 현재 사용 중인 토큰을 블랙리스트에 추가하여 무효화
   * 
   * @throws RuntimeException 토큰이 없거나 검증에 실패한 경우 발생
   */
  public void logout() {
    // 요청 헤더에서 토큰 추출
    String token = jwtTokenProvider.resolveToken(request);
    
    if (token == null) {
      throw new RuntimeException("인증 토큰이 없습니다.");
    }
    
    // 토큰 유효성 검사
    if (!jwtTokenProvider.validateToken(token)) {
      throw new RuntimeException("유효하지 않은 토큰입니다.");
    }
    
    // 토큰을 블랙리스트에 추가
    jwtTokenProvider.blacklistToken(token);
    
    // 현재 인증 컨텍스트 초기화
    SecurityContextHolder.clearContext();
  }
}
