package com.ssafy.where2meow.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRequest {
  private String query;
  private PlanDto plan; // Plan 클래스를 추가로 만듭니다.
}
