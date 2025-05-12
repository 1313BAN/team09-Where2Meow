package com.ssafy.where2meow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
  private String password;
  
  // 아이디 저장 여부 (서버에서 쿠키 생성/삭제 처리)
  private boolean rememberEmail;
  
  // 자동 로그인 여부 (Spring Security의 remember-me와 매핑)
  private boolean rememberMe;
}
