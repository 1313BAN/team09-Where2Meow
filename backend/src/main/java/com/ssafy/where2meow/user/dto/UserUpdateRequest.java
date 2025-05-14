package com.ssafy.where2meow.user.dto;

import com.ssafy.where2meow.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

  @NonNull
  private UUID uuid;

  @NotBlank
  private String name;

  @NotBlank
  private String nickname;

  @NotBlank
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank
  @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
      message = "휴대폰 번호 형식이 올바르지 않습니다.")
  private String phone;

  private String image;

  public void updateEntity(User user) {
    if (name != null) user.setName(name);
    if (nickname != null) user.setNickname(nickname);
    if (email != null) user.setEmail(email);
    if (phone != null) user.setPhone(phone);
    if (image != null) user.setImage(image);
  }
}
