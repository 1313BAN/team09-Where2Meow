package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeResponse {
    private Integer reviewId;
    private Boolean isLiked;
    private Integer likeCount;
}
