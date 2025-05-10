package com.ssafy.where2meow.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private int userId;

  @Column(unique = true, columnDefinition = "BINARY(16)")
  private UUID uuid;

  @PrePersist
  public void generateUuid() {
    if (this.uuid == null) {
      this.uuid = UUID.randomUUID();
    }
  }

  private String name;
  private String nickname;

  @Column(unique = true)
  private String email;

  private String password;
  private String phone;

  @Enumerated(EnumType.STRING)
  private Role role;

  private String image;

  @Column(name = "is_active")
  private Boolean isActive;
}
