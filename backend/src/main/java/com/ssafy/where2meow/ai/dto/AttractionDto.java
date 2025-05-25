package com.ssafy.where2meow.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionDto {
  private String uniqueKey;
  private int attractionId;
  private String attractionName;
  private String addr;
  private double lat;
  private double lng;
  private int categoryId;
  private String categoryName;
  private String date;
  private int attractionOrder;
  private String content;
}
