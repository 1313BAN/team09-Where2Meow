package com.ssafy.where2meow.review.dto;

import com.ssafy.where2meow.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResponse {
    private Integer reviewId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer attractionId;
    private String attractionName;
    private String userNickname;
    private String userImage;
    private Integer likeCount;
    private Boolean isLiked;  // 현재 로그인한 사용자가 좋아요를 눌렀는지 여부
    
    public static ReviewDetailResponse fromEntity(Review review, String attractionName, 
                                                 String userNickname, String userImage, 
                                                 Integer likeCount, Boolean isLiked) {
        return ReviewDetailResponse.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .attractionId(review.getAttractionId())
                .attractionName(attractionName)
                .userNickname(userNickname)
                .userImage(userImage)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}
