package com.ssafy.where2meow.user;

import com.ssafy.where2meow.user.controller.AuthController;
import com.ssafy.where2meow.user.dto.LoginRequest;
import com.ssafy.where2meow.user.dto.LoginResponse;
import com.ssafy.where2meow.user.entity.Role;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import com.ssafy.where2meow.user.security.JwtTokenProvider;
import com.ssafy.where2meow.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
public class AuthTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private AuthController authController;

    private User testUser;
    private LoginRequest loginRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // AuthController 초기화 (UserService를 주입)
        authController = new AuthController(userService);
        
        // 테스트 사용자 생성
        testUser = new User();
        testUser.setUserId(1);
        testUser.setUuid(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setName("테스트 사용자");
        testUser.setRole(Role.USER);
        testUser.setIsActive(true);

        // 로그인 요청 생성
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // 인증 객체 생성
        authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com", 
                "password123",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 모의 객체 설정 (lenient 모드 적용)
        lenient().when(userRepository.findByEmailAndIsActiveTrue(testUser.getEmail())).thenReturn(Optional.of(testUser));
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        lenient().when(jwtTokenProvider.createToken(anyString(), anyString())).thenReturn("test.jwt.token");

        // userService에 모의 객체 주입
        ReflectionTestUtils.setField(userService, "authenticationManager", authenticationManager);
        ReflectionTestUtils.setField(userService, "jwtTokenProvider", jwtTokenProvider);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // When
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);
        
        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        LoginResponse loginResponse = response.getBody();
        assertEquals("test.jwt.token", loginResponse.getToken());
        assertEquals(testUser.getUuid().toString(), loginResponse.getUuid());
        assertEquals(testUser.getName(), loginResponse.getName());
        assertEquals(testUser.getEmail(), loginResponse.getEmail());
        assertEquals(testUser.getRole().name(), loginResponse.getRole());
        
        // userRepository.findByEmailAndIsActiveTrue 메서드가 호출되었는지 확인
        verify(userRepository, times(1)).findByEmailAndIsActiveTrue(loginRequest.getEmail());
        
        // jwtTokenProvider.createToken 메서드가 호출되었는지 확인
        verify(jwtTokenProvider, times(1)).createToken(eq(testUser.getEmail()), eq(testUser.getRole().name()));
        
        // authenticationManager.authenticate 메서드가 호출되었는지 확인
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 로그인 실패 테스트")
    void loginFailWithInvalidEmail() {
        // Given
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail("nonexistent@example.com");
        invalidRequest.setPassword("password123");
        
        lenient().when(userRepository.findByEmailAndIsActiveTrue(invalidRequest.getEmail())).thenReturn(Optional.empty());
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));
        
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(invalidRequest);
        });
        
        assertTrue(exception.getMessage().contains("로그인에 실패하였습니다"));
    }

    @Test
    @DisplayName("비활성화된 사용자 로그인 실패 테스트")
    void loginFailWithInactiveUser() {
        // Given
        User inactiveUser = new User();
        inactiveUser.setUserId(2);
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPassword(passwordEncoder.encode("password123"));
        inactiveUser.setName("비활성 사용자");
        inactiveUser.setRole(Role.USER);
        inactiveUser.setIsActive(false);

        LoginRequest inactiveRequest = new LoginRequest();
        inactiveRequest.setEmail("inactive@example.com");
        inactiveRequest.setPassword("password123");
        
        lenient().when(userRepository.findByEmail(inactiveRequest.getEmail())).thenReturn(Optional.of(inactiveUser));
        lenient().when(userRepository.findByEmailAndIsActiveTrue(inactiveRequest.getEmail())).thenReturn(Optional.empty());
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("User is inactive"));
        
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(inactiveRequest);
        });
        
        assertTrue(exception.getMessage().contains("로그인에 실패하였습니다"));
    }

    @Test
    @DisplayName("잘못된 비밀번호 로그인 실패 테스트")
    void loginFailWithInvalidPassword() {
        // Given
        LoginRequest invalidPasswordRequest = new LoginRequest();
        invalidPasswordRequest.setEmail("test@example.com");
        invalidPasswordRequest.setPassword("wrongpassword");
        
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));
        
        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(invalidPasswordRequest);
        });
        
        assertTrue(exception.getMessage().contains("로그인에 실패하였습니다"));
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // When
        ResponseEntity<Void> response = authController.logout();
        
        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    @DisplayName("JWT 토큰 생성 검증")
    void validateTokenCreation() {
        // When
        ResponseEntity<LoginResponse> response = authController.login(loginRequest);
        LoginResponse loginResponse = response.getBody();
        
        // Then
        assertNotNull(loginResponse);
        assertEquals("test.jwt.token", loginResponse.getToken());
        
        // jwtTokenProvider.createToken 메서드가 호출되었는지 확인
        verify(jwtTokenProvider).createToken(eq(testUser.getEmail()), eq(testUser.getRole().name()));
    }
}
