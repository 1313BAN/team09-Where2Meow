package com.ssafy.where2meow.plan.dto;

import com.ssafy.where2meow.plan.entity.Plan;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDetailResponse {
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

    private int likeCount;
    private boolean isLiked;
    private boolean isBookmarked;

    private List<PlanAttractionResponse> attractions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanAttractionResponse {
        private int planAttractionId;
        private int attractionId;
        private String content;
        private LocalDate date;
        private int attractionOrder;
    }

    public static PlanDetailResponse fromPlan(Plan plan, int likeCount, boolean isLiked, boolean isBookmarked) {
        List<PlanAttractionResponse> attractionResponses = new ArrayList<>();
        
        if (plan.getPlanAttractions() != null) {
            attractionResponses = plan.getPlanAttractions().stream()
                .map(planAttraction -> new PlanAttractionResponse(
                    planAttraction.getPlanAttractionId(),
                    planAttraction.getAttractionId(),
                    planAttraction.getContent(),
                    planAttraction.getDate(),
                    planAttraction.getAttractionOrder()
                ))
                .collect(Collectors.toList());
        }
        
        return PlanDetailResponse.builder()
                .planId(plan.getPlanId())
                .userId(plan.getUserId())
                .title(plan.getTitle())
                .content(plan.getContent())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .viewCount(plan.getViewCount())
                .isPublic(plan.isPublic())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .attractions(attractionResponses)
                .build();
    }
}
