package com.ssafy.where2meow.attraction.dto;

import com.ssafy.where2meow.attraction.entity.AttractionCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttractionCategoryResponse {
  private Integer categoryId;
  private String categoryName;

  public static AttractionCategoryResponse fromEntity(AttractionCategory category) {
    return AttractionCategoryResponse.builder()
        .categoryId(category.getAttractionCategoryId())
        .categoryName(category.getAttractionCategoryName())
        .build();
  }
}
