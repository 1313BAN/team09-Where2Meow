package com.ssafy.where2meow.plan.dto;

import com.ssafy.where2meow.plan.entity.Plan;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {
    private int planId;
    private int userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private int viewCount;
    private boolean isPublic;

    private int likeCount; // 좋아요 수
    private boolean isLiked; // 사용자가 좋아요를 눌렀는지 여부
    private boolean isBookmarked; // 사용자가 북마크를 눌렀는지 여부

    public static PlanResponse fromPlan(Plan plan, int likeCount, boolean isLiked, boolean isBookmarked) {
        return new PlanResponse(
                plan.getPlanId(),
                plan.getUserId(),
                plan.getTitle(),
                plan.getContent(),
                plan.getCreatedAt(),
                plan.getUpdatedAt(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getViewCount(),
                plan.isPublic(),
                likeCount,
                isLiked,
                isBookmarked
        );
    }
}
