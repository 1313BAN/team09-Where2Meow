package com.ssafy.where2meow.review.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListRequest {
    private Integer attractionId;

    @Builder.Default
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    private Integer page = 0;

    @Builder.Default
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortOrder = "DESC";

    // 유효한 정렬 기준 확인
    public String getSortBy() {
        if (sortBy == null || (!sortBy.equals("createdAt") && !sortBy.equals("likeCount"))) {
            return "createdAt";
        }
        return sortBy;
    }

    // 유효한 정렬 방향 확인
    public String getSortOrder() {
        if (sortOrder == null || (!sortOrder.equals("ASC") && !sortOrder.equals("DESC"))) {
            return "DESC";
        }
        return sortOrder;
    }
}
