package com.ssafy.where2meow.user.service;

import com.ssafy.where2meow.user.dto.LoginRequest;
import com.ssafy.where2meow.user.dto.LoginResponse;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import com.ssafy.where2meow.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

  /**
   * 로그인 처리
   *
   * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
   * @return 로그인 응답 (토큰, 사용자 정보)
   * @throws AuthenticationException 인증 실패 시 발생
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

    } catch (Exception e) {
      throw new RuntimeException("로그인에 실패하였습니다: " + e.getMessage());
    }
  }

  /**
   * 로그아웃 처리
   * JWT는 서버 측에서 관리되지 않으므로 클라이언트에서 토큰을 제거하는 것으로 충분
   * 추가적인 보안 조치가 필요하면 토큰 블랙리스트 관리 등을 구현할 수 있음
   */
  public void logout() {
    SecurityContextHolder.clearContext();
  }
}
