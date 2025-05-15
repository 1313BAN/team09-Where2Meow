package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewListResponse {
    private Integer reviewId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private Integer attractionId;
    private String attractionName;
    private String attractionImage;
    private Integer likeCount;
}
