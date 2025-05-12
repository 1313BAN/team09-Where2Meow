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
public class ReviewDto {
    private Integer reviewId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer attractionId;
    private Integer userId;

    /**
     * Review 엔티티를 ReviewDto로 변환
     *
     * @param review Review 엔티티
     * @return ReviewDto 객체
     */
    public static ReviewDto convertDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .attractionId(review.getAttractionId())
                .userId(review.getUserId())
                .build();
    }
}
