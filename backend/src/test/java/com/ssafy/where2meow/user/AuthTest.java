//package com.ssafy.where2meow.user;
//
//import com.ssafy.where2meow.user.controller.AuthController;
//import com.ssafy.where2meow.user.dto.LoginRequest;
//import com.ssafy.where2meow.user.dto.LoginResponse;
//import com.ssafy.where2meow.user.entity.Role;
//import com.ssafy.where2meow.user.entity.User;
//import com.ssafy.where2meow.user.repository.UserRepository;
//import com.ssafy.where2meow.user.token.JwtTokenProvider;
//import com.ssafy.where2meow.user.token.TokenBlacklist;
//import com.ssafy.where2meow.user.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Collections;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith({SpringExtension.class, MockitoExtension.class})
//@SpringBootTest
//public class AuthTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private TokenBlacklist tokenBlacklist;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    private AuthController authController;
//
//    private User testUser;
//    private LoginRequest loginRequest;
//    private Authentication authentication;
//    private final String testToken = "test.jwt.token";
//
//    @BeforeEach
//    void setUp() {
//        // AuthController 초기화 (UserService를 주입)
//        authController = new AuthController(userService);
//
//        // 테스트 사용자 생성
//        testUser = new User();
//        testUser.setUserId(1);
//        testUser.setUuid(UUID.randomUUID());
//        testUser.setEmail("test@example.com");
//        testUser.setPassword(passwordEncoder.encode("password123"));
//        testUser.setName("테스트 사용자");
//        testUser.setRole(Role.USER);
//        testUser.setIsActive(true);
//
//        // 로그인 요청 생성
//        loginRequest = new LoginRequest();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password123");
//
//        // 인증 객체 생성
//        authentication = new UsernamePasswordAuthenticationToken(
//                "test@example.com",
//                "password123",
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
//        );
//
//        // 모의 객체 설정 (lenient 모드 적용)
//        lenient().when(userRepository.findByEmailAndIsActiveTrue(testUser.getEmail())).thenReturn(Optional.of(testUser));
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        lenient().when(jwtTokenProvider.createToken(anyString(), anyString())).thenReturn(testToken);
//
//        // 로그아웃 관련 모의 설정
//        lenient().when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class))).thenReturn(testToken);
//        lenient().when(jwtTokenProvider.validateToken(testToken)).thenReturn(true);
//        lenient().doNothing().when(jwtTokenProvider).blacklistToken(testToken);
//
//        // userService에 모의 객체 주입
//        ReflectionTestUtils.setField(userService, "authenticationManager", authenticationManager);
//        ReflectionTestUtils.setField(userService, "jwtTokenProvider", jwtTokenProvider);
//        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
//        ReflectionTestUtils.setField(userService, "request", request);
//    }
//
//    @Test
//    @DisplayName("로그인 성공 테스트")
//    void loginSuccess() {
//        // When
//        ResponseEntity<LoginResponse> response = authController.login(loginRequest);
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//
//        LoginResponse loginResponse = response.getBody();
//        assertEquals("test.jwt.token", loginResponse.getToken());
//        assertEquals(testUser.getUuid().toString(), loginResponse.getUuid());
//        assertEquals(testUser.getName(), loginResponse.getName());
//        assertEquals(testUser.getEmail(), loginResponse.getEmail());
//        assertEquals(testUser.getRole().name(), loginResponse.getRole());
//
//        // userRepository.findByEmailAndIsActiveTrue 메서드가 호출되었는지 확인
//        verify(userRepository, times(1)).findByEmailAndIsActiveTrue(loginRequest.getEmail());
//
//        // jwtTokenProvider.createToken 메서드가 호출되었는지 확인
//        verify(jwtTokenProvider, times(1)).createToken(eq(testUser.getEmail()), eq(testUser.getRole().name()));
//
//        // authenticationManager.authenticate 메서드가 호출되었는지 확인
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 사용자 로그인 실패 테스트")
//    void loginFailWithInvalidEmail() {
//        // Given
//        LoginRequest invalidRequest = new LoginRequest();
//        invalidRequest.setEmail("nonexistent@example.com");
//        invalidRequest.setPassword("password123");
//
//        lenient().when(userRepository.findByEmailAndIsActiveTrue(invalidRequest.getEmail())).thenReturn(Optional.empty());
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Bad credentials"));
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(invalidRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("이메일 또는 비밀번호가 일치하지 않습니다"));
//    }
//
//    @Test
//    @DisplayName("비활성화된 사용자 로그인 실패 테스트")
//    void loginFailWithInactiveUser() {
//        // Given
//        User inactiveUser = new User();
//        inactiveUser.setUserId(2);
//        inactiveUser.setEmail("inactive@example.com");
//        inactiveUser.setPassword(passwordEncoder.encode("password123"));
//        inactiveUser.setName("비활성 사용자");
//        inactiveUser.setRole(Role.USER);
//        inactiveUser.setIsActive(false);
//
//        LoginRequest inactiveRequest = new LoginRequest();
//        inactiveRequest.setEmail("inactive@example.com");
//        inactiveRequest.setPassword("password123");
//
//        lenient().when(userRepository.findByEmail(inactiveRequest.getEmail())).thenReturn(Optional.of(inactiveUser));
//        lenient().when(userRepository.findByEmailAndIsActiveTrue(inactiveRequest.getEmail())).thenReturn(Optional.empty());
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new DisabledException("User account is disabled"));
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(inactiveRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("계정이 비활성화되어 있습니다"));
//    }
//
//    @Test
//    @DisplayName("잘못된 비밀번호 로그인 실패 테스트")
//    void loginFailWithInvalidPassword() {
//        // Given
//        LoginRequest invalidPasswordRequest = new LoginRequest();
//        invalidPasswordRequest.setEmail("test@example.com");
//        invalidPasswordRequest.setPassword("wrongpassword");
//
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new BadCredentialsException("Bad credentials"));
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(invalidPasswordRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("이메일 또는 비밀번호가 일치하지 않습니다"));
//    }
//
//    @Test
//    @DisplayName("로그아웃 성공 테스트")
//    void logoutSuccessTest() {
//        // Given
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // When
//        ResponseEntity<Void> response = authController.logout();
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//
//        // 토큰 추출 메서드가 호출되었는지 확인
//        verify(jwtTokenProvider).resolveToken(any(HttpServletRequest.class));
//
//        // 토큰 유효성 검증 메서드가 호출되었는지 확인
//        verify(jwtTokenProvider).validateToken(testToken);
//
//        // 토큰 블랙리스트 추가 메서드가 호출되었는지 확인
//        verify(jwtTokenProvider).blacklistToken(testToken);
//    }
//
//    @Test
//    @DisplayName("로그아웃 실패 테스트 - 토큰 없음")
//    void logoutFailWithNoTokenTest() {
//        // Given
//        lenient().when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class))).thenReturn(null);
//
//        // When
//        ResponseEntity<Void> response = authController.logout();
//
//        // Then
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("로그아웃 실패 테스트 - 유효하지 않은 토큰")
//    void logoutFailWithInvalidTokenTest() {
//        // Given
//        lenient().when(jwtTokenProvider.validateToken(testToken)).thenReturn(false);
//
//        // When
//        ResponseEntity<Void> response = authController.logout();
//
//        // Then
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("이미 로그아웃된 토큰으로 재로그아웃 시도")
//    void logoutWithAlreadyBlacklistedTokenTest() {
//        // Given
//        lenient().when(jwtTokenProvider.validateToken(testToken)).thenReturn(false);
//
//        // When
//        ResponseEntity<Void> response = authController.logout();
//
//        // Then
//        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("계정이 잠긴 사용자 로그인 실패 테스트")
//    void loginFailWithLockedAccount() {
//        // Given
//        LoginRequest lockedRequest = new LoginRequest();
//        lockedRequest.setEmail("locked@example.com");
//        lockedRequest.setPassword("password123");
//
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new LockedException("User account is locked"));
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(lockedRequest);
//        });
//
//        assertTrue(exception.getMessage().contains("계정이 잠겨 있습니다"));
//    }
//
//    @Test
//    @DisplayName("기타 인증 예외 로그인 실패 테스트")
//    void loginFailWithOtherAuthenticationException() {
//        // Given
//        LoginRequest request = new LoginRequest();
//        request.setEmail("other@example.com");
//        request.setPassword("password123");
//
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new AuthenticationException("Other authentication error") {});
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(request);
//        });
//
//        assertTrue(exception.getMessage().contains("인증에 실패했습니다"));
//    }
//
//    @Test
//    @DisplayName("기타 예외 로그인 실패 테스트")
//    void loginFailWithOtherException() {
//        // Given
//        LoginRequest request = new LoginRequest();
//        request.setEmail("exception@example.com");
//        request.setPassword("password123");
//
//        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new NullPointerException("Some other error"));
//
//        // When & Then
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            userService.login(request);
//        });
//
//        assertTrue(exception.getMessage().contains("로그인 처리 중 오류가 발생했습니다"));
//    }
//
//    @Test
//    @DisplayName("JWT 토큰 생성 검증")
//    void validateTokenCreation() {
//        // When
//        ResponseEntity<LoginResponse> response = authController.login(loginRequest);
//        LoginResponse loginResponse = response.getBody();
//
//        // Then
//        assertNotNull(loginResponse);
//        assertEquals("test.jwt.token", loginResponse.getToken());
//
//        // jwtTokenProvider.createToken 메서드가 호출되었는지 확인
//        verify(jwtTokenProvider).createToken(eq(testUser.getEmail()), eq(testUser.getRole().name()));
//    }
//}
