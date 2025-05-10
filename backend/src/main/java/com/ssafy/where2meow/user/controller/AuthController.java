package com.ssafy.where2meow.user.controller;

import com.ssafy.where2meow.user.dto.LoginRequest;
import com.ssafy.where2meow.user.dto.LoginResponse;
import com.ssafy.where2meow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 로그인 처리
     * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
     * @return JWT 토큰 및 사용자 정보
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    /**
     * 로그아웃 처리
     * @return 성공 응답 또는 오류 응답
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        try {
            userService.logout();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 인증 토큰이 없거나 유효하지 않은 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
