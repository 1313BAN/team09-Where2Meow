package com.ssafy.where2meow.user.dto;

import com.ssafy.where2meow.user.entity.Role;
import com.ssafy.where2meow.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {

  @NotBlank(message = "이름은 필수 입력값입니다.")
  private String name;

  @NotBlank(message = "닉네임은 필수 입력값입니다.")
  private String nickname;

  @NotBlank(message = "이메일은 필수 입력값입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력값입니다.")
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인란은 필수 입력값입니다.")
  @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다.")
  private String confirmPassword;

  @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
      message = "휴대폰 번호 형식이 올바르지 않습니다.")
  private String phone;

  private String image;

  public User toEntity() {
    return User.builder()
        .name(name)
        .nickname(nickname)
        .email(email)
        .password(password)
        .phone(phone)
        .image(image)
        .role(Role.USER)
        .isActive(true)
        .build();
  }
}
