package com.ssafy.where2meow.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String uuid;
    private String name;
    private String email;
    private String role;
    private String message;  // 추가 정보 메시지
}
