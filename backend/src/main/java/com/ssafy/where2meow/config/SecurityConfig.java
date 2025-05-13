package com.ssafy.where2meow.config;

import com.ssafy.where2meow.user.token.JwtAuthenticationFilter;
import com.ssafy.where2meow.user.token.JwtTokenProvider;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  
  @Value("${argon2.type:ARGON2id}")
  private String argon2Type;
  
  @Value("${argon2.salt-length:16}")
  private int argon2SaltLength;
  
  @Value("${argon2.hash-length:32}")
  private int argon2HashLength;
  
  @Value("${argon2.iterations:4}")
  private int argon2Iterations;
  
  @Value("${argon2.memory:65536}")
  private int argon2Memory;
  
  @Value("${argon2.parallelism:1}")
  private int argon2Parallelism;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .formLogin(formLogin -> formLogin.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/login").permitAll()
            .requestMatchers("/api/auth/logout").authenticated()
            .requestMatchers("/api/plan/user").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/plan/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/plan/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/plan/**").authenticated()
            .anyRequest().permitAll()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // Argon2 인코더 구현
    return new PasswordEncoder() {
      private final Argon2 argon2 = Argon2Factory.create(
          getArgon2Type(argon2Type), // 타입 설정 (설정에서 가져옴)
          argon2SaltLength,  // 솔트 길이 (설정에서 가져옴)
          argon2HashLength); // 해시 길이 (설정에서 가져옴)

      @Override
      public String encode(CharSequence rawPassword) {
        // Argon2 파라미터: 반복 횟수, 메모리 비용, 병렬 처리 횟수
        try {
          return argon2.hash(argon2Iterations, argon2Memory, argon2Parallelism, rawPassword.toString().toCharArray());
        } catch (Exception e) {
          throw new RuntimeException("비밀번호 인코딩 중 오류가 발생했습니다", e);
        }
      }

      @Override
      public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try {
          return argon2.verify(encodedPassword, rawPassword.toString().toCharArray());
        } catch (Exception e) {
          throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다", e);
        }
      }
    };
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
  
  // 문자열 타입을 Argon2Factory.Argon2Types 열거형으로 변환
  private Argon2Factory.Argon2Types getArgon2Type(String type) {
    if ("ARGON2i".equalsIgnoreCase(type)) {
      return Argon2Factory.Argon2Types.ARGON2i;
    } else if ("ARGON2d".equalsIgnoreCase(type)) {
      return Argon2Factory.Argon2Types.ARGON2d;
    } else {
      return Argon2Factory.Argon2Types.ARGON2id; // 기본값
    }
  }
}
