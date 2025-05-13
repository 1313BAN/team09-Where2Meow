package com.ssafy.where2meow.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionListResponse {
  private Integer attractionId;
  private String attractionName;
  private String image;
  private Long reviewCount;
  private Double reviewAvgScore;
  private String stateName;
  private String cityName;
}
