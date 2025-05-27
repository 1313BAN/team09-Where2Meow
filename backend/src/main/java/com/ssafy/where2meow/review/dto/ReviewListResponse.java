package com.ssafy.where2meow.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {
    private Integer reviewId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userNickname;
    private String userImage;
    private UUID userUuid;
    private Integer likeCount;
    private Boolean isLiked;
}
