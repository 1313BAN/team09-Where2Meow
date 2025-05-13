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
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
  private String password;

  @NotBlank
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
  private String confirmPassword;
}
