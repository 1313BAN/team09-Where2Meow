package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewResponse {
  private Integer reviewId;
  private Integer attractionId;
  private String attractionName;
  private String content;
  private Integer score;
  private LocalDateTime updateAt;
  private Integer likeCount;
}
