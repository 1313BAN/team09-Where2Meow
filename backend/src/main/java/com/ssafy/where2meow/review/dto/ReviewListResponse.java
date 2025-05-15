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
public class ReviewListResponse {
    private Integer reviewId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private String userNickname;
    private String userImage;
    private Integer likeCount;
}
