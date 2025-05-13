package com.ssafy.where2meow.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FIndIdRequest {

  @NotBlank(message = "이름은 필수 입력값입니다.")
  private String name;
  @NotBlank(message = "전화번호는 필수 입력값입니다.")
  private String phone;
}
