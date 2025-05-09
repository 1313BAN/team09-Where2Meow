package com.ssafy.where2meow.user.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
  private int userId;
  private UUID uuid;
  private String name;
  private String nickname;
  private String email;
  private String password;
  private String phone;
  private Role role;
  private String image;
  private Boolean isActive;
}
