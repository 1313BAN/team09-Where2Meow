package com.ssafy.where2meow.ai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
  @NotBlank(message = "쿼리는 필수입니다")
  private String query;

  @NotNull(message = "플랜 정보는 필수입니다")
  @Valid
  private PlanDto plan;
}
