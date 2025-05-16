package com.ssafy.where2meow.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UuidRequest {

  @NotNull(message = "사용자 식별자는 필수입니다.")
  private UUID uuid;
}
