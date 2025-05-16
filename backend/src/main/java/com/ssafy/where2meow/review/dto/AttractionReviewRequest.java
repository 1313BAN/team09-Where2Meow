package com.ssafy.where2meow.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttractionReviewRequest {

  @NotNull
  private Integer attractionId;
  @NotNull
  private UUID uuid;
}
