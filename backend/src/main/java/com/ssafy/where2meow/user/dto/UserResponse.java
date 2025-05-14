package com.ssafy.where2meow.user.dto;

import com.ssafy.where2meow.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

  private UUID uuid;
  private String name;
  private String nickname;
  private String email;
  private String phone;
  private String role;
  private String image;
  private Boolean isActive;

  public static UserResponse fromEntity(User user) {
    return UserResponse.builder()
        .uuid(user.getUuid())
        .name(user.getName())
        .nickname(user.getNickname())
        .email(user.getEmail())
        .phone(user.getPhone())
        .role(user.getRole().name())
        .image(user.getImage())
        .isActive(user.getIsActive())
        .build();
  }
}
